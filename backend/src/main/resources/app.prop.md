spring.application.name=backend

# Primary SQL Server DataSource (User Authentication and Menu System)

spring.datasource.sqlserver1.jdbc-url=jdbc:sqlserver://localhost:1433;databaseName=YOUR_DB_NAME;encrypt=true;trustServerCertificate=true
spring.datasource.sqlserver1.username=YOUR_USERNAME
spring.datasource.sqlserver1.password=YOUR_PASSWORD
spring.datasource.sqlserver1.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# Secondary SQL Server DataSource (IASS Business Data)

spring.datasource.sqlserver2.jdbc-url=jdbc:sqlserver://localhost:1433;databaseName=YOUR_SECONDARY_DB;encrypt=true;trustServerCertificate=true
spring.datasource.sqlserver2.username=YOUR_USERNAME
spring.datasource.sqlserver2.password=YOUR_PASSWORD
spring.datasource.sqlserver2.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA settings

spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging for Hibernate SQL queries

logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
