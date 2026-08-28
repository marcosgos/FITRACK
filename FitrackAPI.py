from flask import Flask, jsonify, request
import mysql.connector
import os

app = Flask(__name__)

CA_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), "ca.pem")


def get_connection():
    return mysql.connector.connect(
        host=os.environ.get("AIVEN_HOST", "fitrack-db-marcutigon-1b0a.f.aivencloud.com"),
        port=int(os.environ.get("AIVEN_PORT", 15485)),
        user=os.environ.get("AIVEN_USER", "avnadmin"),
        password=os.environ["AIVEN_PASSWORD"],
        database=os.environ.get("AIVEN_DB", "fitrack"),
        ssl_ca=CA_PATH,
        ssl_verify_cert=True
    )

@app.route('/login', methods=['POST'])
def login():
    body = request.get_json()
    correo = body.get('correo')
    contrasena = body.get('contrasena')

    if not correo or not contrasena:
        return jsonify({"error": "Correo y contraseña son obligatorios"}), 400

    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute(
        "SELECT id_usuario, nombre, edad, peso, correo, objetivo_pasos, fecha_registro, contrasena "
        "FROM usuarios WHERE correo = %s",
        (correo,)
    )
    usuario = cursor.fetchone()
    cursor.close()
    conn.close()

    if usuario is None or usuario['contrasena'] != contrasena:
        return jsonify({"error": "Correo o contraseña incorrectos"}), 401

    # No devolvemos la contraseña en la respuesta
    usuario.pop('contrasena')
    return jsonify(usuario), 200


@app.route('/usuarios', methods=['GET'])
def get_usuarios():
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT id_usuario, nombre, edad, peso, correo, objetivo_pasos, fecha_registro FROM usuarios")
    datos = cursor.fetchall()
    cursor.close()
    conn.close()
    return jsonify(datos)


@app.route('/usuarios/<int:id_usuario>', methods=['GET'])
def get_usuario(id_usuario):
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute(
        "SELECT id_usuario, nombre, edad, peso, correo, objetivo_pasos, fecha_registro "
        "FROM usuarios WHERE id_usuario = %s",
        (id_usuario,)
    )
    dato = cursor.fetchone()
    cursor.close()
    conn.close()
    if dato is None:
        return jsonify({"error": "Usuario no encontrado"}), 404
    return jsonify(dato)


@app.route('/usuarios', methods=['POST'])
def add_usuario():
    body = request.get_json()
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO usuarios (nombre, edad, peso, correo, contrasena, objetivo_pasos) "
        "VALUES (%s, %s, %s, %s, %s, %s)",
        (
            body['nombre'],
            body['edad'],
            body['peso'],
            body['correo'],
            body['contrasena'],
            body.get('objetivo_pasos', 10000)
        )
    )
    conn.commit()
    nuevo_id = cursor.lastrowid
    cursor.close()
    conn.close()
    return jsonify({"id": nuevo_id}), 201


@app.route('/usuarios/<int:id_usuario>/actividad', methods=['GET'])
def get_actividad(id_usuario):
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute(
        "SELECT * FROM actividad_diaria WHERE id_usuario = %s ORDER BY fecha DESC",
        (id_usuario,)
    )
    datos = cursor.fetchall()
    cursor.close()
    conn.close()
    return jsonify(datos)


@app.route('/usuarios/<int:id_usuario>/actividad', methods=['POST'])
def add_actividad(id_usuario):
    body = request.get_json()
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO actividad_diaria (id_usuario, fecha, pasos_diarios, calorias_quemadas, horas_sueno) "
        "VALUES (%s, %s, %s, %s, %s)",
        (
            id_usuario,
            body['fecha'],
            body.get('pasos_diarios', 0),
            body.get('calorias_quemadas', 0),
            body.get('horas_sueno', 0)
        )
    )
    conn.commit()
    nuevo_id = cursor.lastrowid
    cursor.close()
    conn.close()
    return jsonify({"id": nuevo_id}), 201


@app.route('/tipos_entrenamiento', methods=['GET'])
def get_tipos_entrenamiento():
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM tipos_entrenamiento")
    datos = cursor.fetchall()
    cursor.close()
    conn.close()
    return jsonify(datos)


@app.route('/usuarios/<int:id_usuario>/entrenamientos', methods=['GET'])
def get_entrenamientos(id_usuario):
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute(
        "SELECT e.id_entrenamiento, e.fecha_inicio, e.duracion_segundos, "
        "e.frecuencia_cardiaca, e.pasos, e.calorias_quemadas, t.nombre AS tipo "
        "FROM entrenamientos e "
        "JOIN tipos_entrenamiento t ON e.id_tipo = t.id_tipo "
        "WHERE e.id_usuario = %s "
        "ORDER BY e.fecha_inicio DESC",
        (id_usuario,)
    )
    datos = cursor.fetchall()
    cursor.close()
    conn.close()
    return jsonify(datos)


@app.route('/usuarios/<int:id_usuario>/entrenamientos', methods=['POST'])
def add_entrenamiento(id_usuario):
    body = request.get_json()
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO entrenamientos "
        "(id_usuario, id_tipo, fecha_inicio, duracion_segundos, frecuencia_cardiaca, pasos, calorias_quemadas) "
        "VALUES (%s, %s, %s, %s, %s, %s, %s)",
        (
            id_usuario,
            body['id_tipo'],
            body['fecha_inicio'],
            body.get('duracion_segundos', 0),
            body['frecuencia_cardiaca'],
            body.get('pasos', 0),
            body.get('calorias_quemadas', 0)
        )
    )
    conn.commit()
    nuevo_id = cursor.lastrowid
    cursor.close()
    conn.close()
    return jsonify({"id": nuevo_id}), 201


@app.route('/usuarios/<int:id_usuario>/historial_peso', methods=['GET'])
def get_historial_peso(id_usuario):
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute(
        "SELECT * FROM historial_peso WHERE id_usuario = %s ORDER BY fecha ASC",
        (id_usuario,)
    )
    datos = cursor.fetchall()
    cursor.close()
    conn.close()
    return jsonify(datos)


@app.route('/usuarios/<int:id_usuario>/historial_peso', methods=['POST'])
def add_historial_peso(id_usuario):
    body = request.get_json()
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO historial_peso (id_usuario, peso, fecha) VALUES (%s, %s, %s)",
        (id_usuario, body['peso'], body['fecha'])
    )
    conn.commit()
    nuevo_id = cursor.lastrowid
    cursor.close()
    conn.close()
    return jsonify({"id": nuevo_id}), 201


@app.route('/basedatos', methods=['GET'])
def get_basedatos_completa():
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)

    cursor.execute("SELECT id_usuario, nombre, edad, peso, correo, objetivo_pasos, fecha_registro FROM usuarios")
    usuarios = cursor.fetchall()

    cursor.execute("SELECT * FROM historial_peso")
    historial_peso = cursor.fetchall()

    cursor.execute("SELECT * FROM actividad_diaria")
    actividad_diaria = cursor.fetchall()

    cursor.execute("SELECT * FROM tipos_entrenamiento")
    tipos_entrenamiento = cursor.fetchall()

    cursor.execute(
        "SELECT e.*, t.nombre AS tipo "
        "FROM entrenamientos e "
        "JOIN tipos_entrenamiento t ON e.id_tipo = t.id_tipo"
    )
    entrenamientos = cursor.fetchall()

    cursor.close()
    conn.close()

    return jsonify({
        "usuarios": usuarios,
        "historial_peso": historial_peso,
        "actividad_diaria": actividad_diaria,
        "tipos_entrenamiento": tipos_entrenamiento,
        "entrenamientos": entrenamientos
    })


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
