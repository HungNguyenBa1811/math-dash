// Lớp thực thể dùng chung cho app Android và server
plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    // @JsonIgnore, cùng phiên bản Jackson mà Spring Boot 3.3.5 quản lý
    api("com.fasterxml.jackson.core:jackson-annotations:2.17.2")
}
