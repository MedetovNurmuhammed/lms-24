//package lms.config.flyway;
//
//import org.flywaydb.core.Flyway;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class FlywayConfig {
//    @Value("${spring.flyway.url}")
//    private String dataSourceUrl;
//    @Value("${spring.flyway.user}")
//    private String dataSourceUsername;
//    @Value("${spring.flyway.password}")
//    private String dataSourcePassword;
//
//    @Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setUrl(dataSourceUrl);
//        dataSource.setUsername(dataSourceUsername);
//        dataSource.setPassword(dataSourcePassword);
//        return dataSource;
//    }
//
//    @Bean(initMethod = "migrate")
//    @DependsOn("dataSource")
//    public Flyway flyway(DataSource dataSource) {
//        return Flyway.configure()
//                .dataSource(dataSource)
//                .locations("classpath:db.migration")
//                .baselineOnMigrate(true)
//                .load();
//    }
//
//    @Bean
//    public FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
//        return new FlywayMigrationInitializer(flyway, null);
//    }
//}