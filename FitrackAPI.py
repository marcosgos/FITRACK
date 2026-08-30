"""
FITRACK · REST API (Flask + MySQL/Aiven)

English, transactional, and structured so new resources are easy to add.
Dates are formatted in SQL (DATE_FORMAT) so the JSON shape is stable
regardless of the Flask/JSON-encoder version:
    dates      -> "YYYY-MM-DD"
    datetimes  -> "YYYY-MM-DD HH:MM:SS"
"""

from flask import Flask, jsonify, request
from werkzeug.security import generate_password_hash, check_password_hash
# NUEVO (login con Google): verifica el ID token que manda la app tras el
# flujo de Credential Manager, contra el Web Client ID de Google Cloud.
from google.oauth2 import id_token as google_id_token
from google.auth.transport import requests as google_requests
import mysql.connector
import os
from datetime import date, datetime

app = Flask(__name__)

CA_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), "ca.pem")

# NUEVO: Client ID de tipo "Web application" creado en Google Cloud Console.
# Es el mismo valor que la app Android usa como setServerClientId(...) en
# Credential Manager (GoogleAuthHelper.kt) — así el token que emite Google
# se puede verificar aquí contra la misma audiencia.
GOOGLE_WEB_CLIENT_ID = os.environ.get("GOOGLE_WEB_CLIENT_ID")

# Reusable SQL fragments so date formatting stays consistent everywhere.
# FIX: mysql-connector-python's C extension scans the raw SQL text with a
# dumb regex (`%s`) to find placeholders to bind params against — with no
# `%%`-escaping support for positional params (that only exists for the
# dict/named-param path). A literal '%s' inside a DATE_FORMAT mask (from
# '%H:%i:%s', the seconds specifier) was being mistaken for an extra
# placeholder, raising "Not enough parameters for the SQL statement" on
# every query that combined USER_COLUMNS with a real bound parameter
# (login, get_user, /auth/google...). Fix: use '%S' instead of '%s' for
# seconds — MySQL treats them as exact synonyms, but '%S' doesn't match
# the connector's placeholder regex.
USER_COLUMNS = (
    "user_id, name, "
    "DATE_FORMAT(date_of_birth, '%Y-%m-%d') AS date_of_birth, "
    "sex, height_cm, weight_kg, email, daily_step_goal, "
    "DATE_FORMAT(created_at, '%Y-%m-%d %H:%i:%S') AS created_at"
)


# ---------------------------------------------------------------------
#  Infrastructure
# ---------------------------------------------------------------------
def get_connection():
    return mysql.connector.connect(
        host=os.environ.get("AIVEN_HOST", "fitrack-db-marcutigon-1b0a.f.aivencloud.com"),
        port=int(os.environ.get("AIVEN_PORT", 15485)),
        user=os.environ.get("AIVEN_USER", "avnadmin"),
        password=os.environ["AIVEN_PASSWORD"],
        database=os.environ.get("AIVEN_DB", "fitrack"),
        ssl_ca=CA_PATH,
        ssl_verify_cert=True,
    )


def compute_age(date_of_birth):
    """Derive age from a 'YYYY-MM-DD' string (or None)."""
    if not date_of_birth:
        return None
    try:
        born = datetime.strptime(date_of_birth, "%Y-%m-%d").date()
    except (ValueError, TypeError):
        return None
    today = date.today()
    return today.year - born.year - ((today.month, today.day) < (born.month, born.day))


def with_age(user):
    """Attach a derived 'age' field to a user dict for client convenience."""
    if user is not None:
        user["age"] = compute_age(user.get("date_of_birth"))
    return user


# ---------------------------------------------------------------------
#  Auth
# ---------------------------------------------------------------------
@app.route('/login', methods=['POST'])
def login():
    body = request.get_json(silent=True) or {}
    email = body.get('email')
    password = body.get('password')

    if not email or not password:
        return jsonify({"error": "Email and password are required"}), 400

    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        cursor.execute(
            f"SELECT {USER_COLUMNS}, password_hash FROM users WHERE email = %s",
            (email,),
        )
        user = cursor.fetchone()
    finally:
        cursor.close()
        conn.close()

    # NUEVO: las cuentas creadas solo con Google no tienen password_hash
    # (columna ahora NULLable); sin este check, check_password_hash(None, ...)
    # lanzaría un TypeError en vez de devolver el 401 esperado.
    if user is None or user['password_hash'] is None or not check_password_hash(user['password_hash'], password):
        return jsonify({"error": "Incorrect email or password"}), 401

    user.pop('password_hash')
    return jsonify(with_age(user)), 200


# ---------------------------------------------------------------------
#  Auth · Google Sign-In
#  Un único endpoint para login y registro: Google no distingue entre
#  ambos, así que la primera vez que se usa una cuenta se crea la fila
#  y las siguientes veces simplemente se recupera.
# ---------------------------------------------------------------------
@app.route('/auth/google', methods=['POST'])
def login_with_google():
    body = request.get_json(silent=True) or {}
    token = body.get('id_token')
    if not token:
        return jsonify({"error": "id_token is required"}), 400

    try:
        payload = google_id_token.verify_oauth2_token(
            token, google_requests.Request(), GOOGLE_WEB_CLIENT_ID
        )
    except ValueError:
        return jsonify({"error": "Invalid Google token"}), 401

    if not payload.get('email_verified', False):
        return jsonify({"error": "Google email is not verified"}), 401

    google_id = payload['sub']
    email = payload['email']
    name = payload.get('name') or email.split('@')[0]

    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        cursor.execute(f"SELECT {USER_COLUMNS} FROM users WHERE google_id = %s", (google_id,))
        user = cursor.fetchone()

        if user is None:
            # Sin cuenta vinculada a este google_id todavía: miramos por email
            # para enlazar una cuenta de contraseña ya existente en vez de
            # duplicarla.
            cursor.execute(f"SELECT {USER_COLUMNS} FROM users WHERE email = %s", (email,))
            user = cursor.fetchone()

            if user is None:
                cursor.execute(
                    "INSERT INTO users (name, email, google_id, daily_step_goal) "
                    "VALUES (%s, %s, %s, %s)",
                    (name, email, google_id, 10000),
                )
                conn.commit()
                cursor.execute(f"SELECT {USER_COLUMNS} FROM users WHERE user_id = %s", (cursor.lastrowid,))
                user = cursor.fetchone()
            else:
                cursor.execute("UPDATE users SET google_id = %s WHERE user_id = %s", (google_id, user['user_id']))
                conn.commit()
    finally:
        cursor.close()
        conn.close()

    return jsonify(with_age(user)), 200


# ---------------------------------------------------------------------
#  Users
# ---------------------------------------------------------------------
@app.route('/users', methods=['GET'])
def get_users():
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        cursor.execute(f"SELECT {USER_COLUMNS} FROM users")
        rows = cursor.fetchall()
    finally:
        cursor.close()
        conn.close()
    return jsonify([with_age(u) for u in rows])


@app.route('/users/<int:user_id>', methods=['GET'])
def get_user(user_id):
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        cursor.execute(f"SELECT {USER_COLUMNS} FROM users WHERE user_id = %s", (user_id,))
        row = cursor.fetchone()
    finally:
        cursor.close()
        conn.close()
    if row is None:
        return jsonify({"error": "User not found"}), 404
    return jsonify(with_age(row))


@app.route('/users', methods=['POST'])
def add_user():
    body = request.get_json(silent=True) or {}
    required = ('name', 'email', 'password')
    missing = [f for f in required if not body.get(f)]
    if missing:
        return jsonify({"error": f"Missing fields: {', '.join(missing)}"}), 400

    conn = get_connection()
    cursor = conn.cursor()
    try:
        cursor.execute(
            "INSERT INTO users "
            "(name, date_of_birth, sex, height_cm, weight_kg, email, password_hash, daily_step_goal) "
            "VALUES (%s, %s, %s, %s, %s, %s, %s, %s)",
            (
                body['name'],
                body.get('date_of_birth'),
                body.get('sex'),
                body.get('height_cm'),
                body.get('weight_kg'),
                body['email'],
                generate_password_hash(body['password']),
                body.get('daily_step_goal', 10000),
            ),
        )
        conn.commit()
        new_id = cursor.lastrowid
    except mysql.connector.IntegrityError:
        return jsonify({"error": "Email already registered"}), 409
    finally:
        cursor.close()
        conn.close()
    return jsonify({"id": new_id}), 201


@app.route('/users/<int:user_id>', methods=['PATCH'])
def update_user(user_id):
    """Partial profile update for the personal-data screen. Only the
    fields present in the body are changed; everything else is left as-is."""
    body = request.get_json(silent=True) or {}
    editable = ('name', 'date_of_birth', 'sex', 'height_cm', 'weight_kg', 'daily_step_goal')
    updates = {f: body[f] for f in editable if f in body}

    if not updates:
        return jsonify({"error": "No updatable fields provided"}), 400

    set_clause = ", ".join(f"{col} = %s" for col in updates)
    values = list(updates.values()) + [user_id]

    conn = get_connection()
    cursor = conn.cursor()
    try:
        cursor.execute(f"UPDATE users SET {set_clause} WHERE user_id = %s", values)
        conn.commit()
        affected = cursor.rowcount
    finally:
        cursor.close()
        conn.close()
    if affected == 0:
        return jsonify({"error": "User not found"}), 404
    return jsonify({"updated": list(updates.keys())}), 200


# ---------------------------------------------------------------------
#  Daily activity
# ---------------------------------------------------------------------
@app.route('/users/<int:user_id>/activity', methods=['GET'])
def get_activity(user_id):
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        cursor.execute(
            "SELECT activity_id, user_id, "
            "DATE_FORMAT(activity_date, '%Y-%m-%d') AS activity_date, "
            "steps, calories_burned, sleep_hours "
            "FROM daily_activity WHERE user_id = %s ORDER BY activity_date DESC",
            (user_id,),
        )
        rows = cursor.fetchall()
    finally:
        cursor.close()
        conn.close()
    return jsonify(rows)


@app.route('/users/<int:user_id>/activity', methods=['POST'])
def add_activity(user_id):
    body = request.get_json(silent=True) or {}
    conn = get_connection()
    cursor = conn.cursor()
    try:
        cursor.execute(
            "INSERT INTO daily_activity (user_id, activity_date, steps, calories_burned, sleep_hours) "
            "VALUES (%s, %s, %s, %s, %s)",
            (
                user_id,
                body['activity_date'],
                body.get('steps', 0),
                body.get('calories_burned', 0),
                body.get('sleep_hours', 0),
            ),
        )
        conn.commit()
        new_id = cursor.lastrowid
    finally:
        cursor.close()
        conn.close()
    return jsonify({"id": new_id}), 201


# ---------------------------------------------------------------------
#  Weight history
# ---------------------------------------------------------------------
@app.route('/users/<int:user_id>/weight-history', methods=['GET'])
def get_weight_history(user_id):
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        cursor.execute(
            "SELECT weight_log_id, user_id, weight_kg, "
            "DATE_FORMAT(recorded_on, '%Y-%m-%d') AS recorded_on "
            "FROM weight_history WHERE user_id = %s ORDER BY recorded_on ASC",
            (user_id,),
        )
        rows = cursor.fetchall()
    finally:
        cursor.close()
        conn.close()
    return jsonify(rows)


@app.route('/users/<int:user_id>/weight-history', methods=['POST'])
def add_weight(user_id):
    body = request.get_json(silent=True) or {}
    conn = get_connection()
    cursor = conn.cursor()
    try:
        cursor.execute(
            "INSERT INTO weight_history (user_id, weight_kg, recorded_on) VALUES (%s, %s, %s)",
            (user_id, body['weight_kg'], body['recorded_on']),
        )
        conn.commit()
        new_id = cursor.lastrowid
    finally:
        cursor.close()
        conn.close()
    return jsonify({"id": new_id}), 201


# ---------------------------------------------------------------------
#  Workout types
# ---------------------------------------------------------------------
@app.route('/workout-types', methods=['GET'])
def get_workout_types():
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        cursor.execute("SELECT workout_type_id, code, name FROM workout_types ORDER BY workout_type_id")
        rows = cursor.fetchall()
    finally:
        cursor.close()
        conn.close()
    return jsonify(rows)


# ---------------------------------------------------------------------
#  Workouts
# ---------------------------------------------------------------------
@app.route('/users/<int:user_id>/workouts', methods=['GET'])
def get_workouts(user_id):
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        cursor.execute(
            "SELECT w.workout_id, "
            "DATE_FORMAT(w.started_at, '%Y-%m-%d %H:%i:%S') AS started_at, "
            "w.duration_seconds, w.avg_heart_rate, w.steps, w.calories_burned, "
            "w.notes, w.is_personal_record, w.pr_exercise, w.pr_result, "
            "t.code AS type_code, t.name AS type_name "
            "FROM workouts w "
            "JOIN workout_types t ON w.workout_type_id = t.workout_type_id "
            "WHERE w.user_id = %s ORDER BY w.started_at DESC",
            (user_id,),
        )
        rows = cursor.fetchall()
    finally:
        cursor.close()
        conn.close()
    return jsonify(rows)


@app.route('/users/<int:user_id>/workouts/<int:workout_id>', methods=['GET'])
def get_workout_detail(user_id, workout_id):
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        cursor.execute(
            "SELECT w.workout_id, "
            "DATE_FORMAT(w.started_at, '%Y-%m-%d %H:%i:%S') AS started_at, "
            "w.duration_seconds, w.avg_heart_rate, w.steps, w.calories_burned, "
            "w.notes, w.is_personal_record, w.pr_exercise, w.pr_result, "
            "t.code AS type_code, t.name AS type_name "
            "FROM workouts w "
            "JOIN workout_types t ON w.workout_type_id = t.workout_type_id "
            "WHERE w.user_id = %s AND w.workout_id = %s",
            (user_id, workout_id),
        )
        workout = cursor.fetchone()
        if workout is None:
            return jsonify({"error": "Workout not found"}), 404

        cursor.execute(
            "SELECT exercise_id, position, name, sets, reps, weight_kg "
            "FROM workout_exercises WHERE workout_id = %s ORDER BY position",
            (workout_id,),
        )
        workout["exercises"] = cursor.fetchall()

        cursor.execute(
            "SELECT segment_id, position, duration_seconds, distance_m, note "
            "FROM workout_segments WHERE workout_id = %s ORDER BY position",
            (workout_id,),
        )
        workout["segments"] = cursor.fetchall()
    finally:
        cursor.close()
        conn.close()
    return jsonify(workout)


@app.route('/users/<int:user_id>/workouts', methods=['POST'])
def add_workout(user_id):
    """Create a workout and, in the same transaction, its optional
    strength `exercises` and running `segments`. The type is resolved
    from `type_code` (preferred) or `workout_type_id`."""
    body = request.get_json(silent=True) or {}

    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        # Resolve the workout type by stable code (falls back to id).
        type_id = body.get('workout_type_id')
        if body.get('type_code'):
            cursor.execute(
                "SELECT workout_type_id FROM workout_types WHERE code = %s",
                (body['type_code'],),
            )
            found = cursor.fetchone()
            if found is None:
                return jsonify({"error": f"Unknown workout type '{body['type_code']}'"}), 400
            type_id = found['workout_type_id']
        if type_id is None:
            return jsonify({"error": "workout_type_id or type_code is required"}), 400

        cursor.execute(
            "INSERT INTO workouts "
            "(user_id, workout_type_id, started_at, duration_seconds, avg_heart_rate, "
            " steps, calories_burned, notes, is_personal_record, pr_exercise, pr_result) "
            "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
            (
                user_id,
                type_id,
                body['started_at'],
                body.get('duration_seconds', 0),
                body.get('avg_heart_rate'),
                body.get('steps', 0),
                body.get('calories_burned', 0),
                body.get('notes'),
                body.get('is_personal_record', False),
                body.get('pr_exercise'),
                body.get('pr_result'),
            ),
        )
        workout_id = cursor.lastrowid

        for i, ex in enumerate(body.get('exercises', []), start=1):
            cursor.execute(
                "INSERT INTO workout_exercises (workout_id, position, name, sets, reps, weight_kg) "
                "VALUES (%s, %s, %s, %s, %s, %s)",
                (
                    workout_id,
                    ex.get('position', i),
                    ex.get('name', ''),
                    ex.get('sets', 0),
                    ex.get('reps', 0),
                    ex.get('weight_kg', 0),
                ),
            )

        for i, seg in enumerate(body.get('segments', []), start=1):
            cursor.execute(
                "INSERT INTO workout_segments (workout_id, position, duration_seconds, distance_m, note) "
                "VALUES (%s, %s, %s, %s, %s)",
                (
                    workout_id,
                    seg.get('position', i),
                    seg.get('duration_seconds', 0),
                    seg.get('distance_m'),
                    seg.get('note'),
                ),
            )

        conn.commit()
    except mysql.connector.Error as err:
        conn.rollback()
        return jsonify({"error": f"Database error: {err.msg}"}), 400
    finally:
        cursor.close()
        conn.close()
    return jsonify({"id": workout_id}), 201


# ---------------------------------------------------------------------
#  Full dump (debug / admin)
# ---------------------------------------------------------------------
@app.route('/database', methods=['GET'])
def get_full_database():
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        cursor.execute(f"SELECT {USER_COLUMNS} FROM users")
        users = [with_age(u) for u in cursor.fetchall()]

        cursor.execute(
            "SELECT weight_log_id, user_id, weight_kg, "
            "DATE_FORMAT(recorded_on, '%Y-%m-%d') AS recorded_on FROM weight_history"
        )
        weight_history = cursor.fetchall()

        cursor.execute(
            "SELECT activity_id, user_id, "
            "DATE_FORMAT(activity_date, '%Y-%m-%d') AS activity_date, "
            "steps, calories_burned, sleep_hours FROM daily_activity"
        )
        daily_activity = cursor.fetchall()

        cursor.execute("SELECT workout_type_id, code, name FROM workout_types")
        workout_types = cursor.fetchall()

        cursor.execute(
            "SELECT w.workout_id, w.user_id, w.workout_type_id, "
            "DATE_FORMAT(w.started_at, '%Y-%m-%d %H:%i:%S') AS started_at, "
            "w.duration_seconds, w.avg_heart_rate, w.steps, w.calories_burned, "
            "w.notes, w.is_personal_record, w.pr_exercise, w.pr_result, "
            "t.code AS type_code, t.name AS type_name "
            "FROM workouts w JOIN workout_types t ON w.workout_type_id = t.workout_type_id"
        )
        workouts = cursor.fetchall()

        cursor.execute("SELECT * FROM workout_exercises ORDER BY workout_id, position")
        workout_exercises = cursor.fetchall()

        cursor.execute("SELECT * FROM workout_segments ORDER BY workout_id, position")
        workout_segments = cursor.fetchall()
    finally:
        cursor.close()
        conn.close()

    return jsonify({
        "users": users,
        "weight_history": weight_history,
        "daily_activity": daily_activity,
        "workout_types": workout_types,
        "workouts": workouts,
        "workout_exercises": workout_exercises,
        "workout_segments": workout_segments,
    })


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
