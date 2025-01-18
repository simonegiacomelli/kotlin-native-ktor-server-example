FROM docker.io/library/gradle:8.12.0-jdk21 as build
RUN mkdir /project
COPY . /project
WORKDIR /project
RUN ./gradlew clean linkReleaseExecutableLinuxX64

FROM docker.io/library/ubuntu:24.10
RUN mkdir -p /usr/local/bin
COPY --from=build /project/build/bin/linuxX64/releaseExecutable/app.kexe /usr/local/bin/users-crud-service
ENTRYPOINT [ "/usr/local/bin/users-crud-service" ]