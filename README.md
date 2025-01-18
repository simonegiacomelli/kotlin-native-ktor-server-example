## Kotlin/Native Ktor server example

Hello! This is an example of a REST API server built with Ktor with
Kotlin/Native, with basic CRUD operations implemented with a mutable
list as backing data source.

You can run the server locally with Gradle task `runDebugExecutableLinuxX64`
on Linux systems. The application will listen to port 8080 (you can change
the port in `src/nativeMain/kotlin/Main.kt`). You can check the responses
using the Postman collection  provided in the repository 
`Kotlin_Native_Ktor_Server_Example.postman_collection.json`.

To build a standalone binary 
for Linux, use`linkReleaseExecutableLinuxX64` task. You can locale the binary
at`./build/bin/linuxX64/releaseExecutable/app.kexec`.

## Testing

Test cases are also added to test the CRUD operations. The tests are located
at `./src/nativeTest/kotlin/ApplicationTest.kt`
