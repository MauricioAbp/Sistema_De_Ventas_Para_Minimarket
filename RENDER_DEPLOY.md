# Despliegue gratuito en Render

El proyecto se despliega como un unico servicio: Spring Boot sirve la API y los archivos compilados de Angular desde el mismo dominio.

## 1. Crear la base de datos

Crea una instancia gratuita **TiDB Cloud Starter** y una base llamada `minimarket`. Copia los datos de conexion publica.

La URL JDBC debe tener este formato:

```text
jdbc:mysql://HOST:4000/minimarket?sslMode=VERIFY_IDENTITY
```

## 2. Subir el repositorio a GitHub

No subas contrasenas ni archivos `.env`. El despliegue usa las variables declaradas en `render.yaml`.

## 3. Crear el Blueprint en Render

1. En Render, elige **New > Blueprint**.
2. Conecta este repositorio.
3. Render detectara `render.yaml` y solicitara estas variables:
   - `DB_URL`: URL JDBC de TiDB.
   - `DB_USERNAME`: usuario de TiDB.
   - `DB_PASSWORD`: contrasena de TiDB.
   - `ADMIN_PASSWORD`: contrasena temporal segura para el usuario `admin`.
4. Crea el Blueprint y espera a que `/health` aparezca saludable.

`JWT_SECRET` se genera automaticamente y no debe compartirse.

## 4. Ingresar

Abre la URL `https://minimarket-sowad.onrender.com` que muestre Render e ingresa con:

- Usuario: `admin`
- Contrasena: el valor configurado en `ADMIN_PASSWORD`

En el plan gratuito, Render suspende el servicio tras un periodo sin trafico. La primera carga posterior puede tardar cerca de un minuto.

## Desarrollo local

El frontend sigue usando `proxy.conf.json`, por lo que `npm start` envia las llamadas de API a `http://localhost:8080`.

Antes de iniciar el backend define al menos:

```text
DB_URL=jdbc:mysql://localhost:3306/minimarket
DB_USERNAME=root
DB_PASSWORD=tu_contrasena
JWT_SECRET=una_clave_aleatoria_de_32_caracteres_o_mas
ADMIN_USERNAME=admin
ADMIN_PASSWORD=una_contrasena_segura
```
