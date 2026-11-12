todo_app
========

Installs a Java runtime, copies the todo-api JAR to `app_dir`, installs an
init script and starts the app as a service. The `Restart todo-api` handler
only runs when the JAR or the script changes.

Variables (defaults/main.yml): `app_name`, `app_port`, `app_dir`, `app_user`,
`java_package`, `app_message`. `app_jar` (path to the built JAR) must be set
by the playbook.
