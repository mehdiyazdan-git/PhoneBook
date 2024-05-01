package com.pishgaman.phonebook;

import com.pishgaman.phonebook.entities.AppSettings;
import com.pishgaman.phonebook.entities.LetterType;
import com.pishgaman.phonebook.entities.Position;
import com.pishgaman.phonebook.entities.Year;
import com.pishgaman.phonebook.repositories.AppSettingsRepository;
import com.pishgaman.phonebook.repositories.LetterTypeRepository;
import com.pishgaman.phonebook.repositories.PositionRepository;
import com.pishgaman.phonebook.repositories.YearRepository;
import com.pishgaman.phonebook.security.auth.AuthenticationService;
import com.pishgaman.phonebook.security.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import static com.pishgaman.phonebook.enums.BackupLocation.getDefaultLocation;
import static com.pishgaman.phonebook.security.user.Role.ADMIN;

@SpringBootApplication
@RequiredArgsConstructor
public class PhoneBookApplication {
    private final AppSettingsRepository appSettingsRepository;
    private final YearRepository yearRepository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${vmware.vsphere.url}")
    private String serverUrl;

    @Value("${vmware.vsphere.username}")
    private String username;

    @Value("${vmware.vsphere.password}")
    private String password;
    public static void main(String[] args) {
        SpringApplication.run(PhoneBookApplication.class, args);
    }
    @Bean
    public CommandLineRunner loadData(AuthenticationService service) {
        return args -> {

            var admin = RegisterRequest.builder()
                    .firstname("مدیر")
                    .lastname("سیستم")
                    .username("administrator")
                    .email("yazdanparast.centos@gmail.com")
                    .password("PishGaman123@@")
                    .role(ADMIN)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .enabled(true)
                    .build();


            if (service.isTableEmpty()){
                System.out.println("Admin token: " + service.register(admin).getAccessToken());
            }
            if (appSettingsRepository.count() == 0){
                AppSettings appSettings = new AppSettings();
                appSettings.setMaxUploadFileSize(1048576);
                appSettings.setVsphereUrl(serverUrl);
                appSettings.setVsphereUsername(username);
                appSettings.setVspherePassword(password);
                appSettings.setBackupPath(getDefaultLocation());
                appSettings.setDatabaseName("postgres");
                appSettingsRepository.save(appSettings);
            }
        };
    }

    @Bean
    public CommandLineRunner populateYearTable(YearRepository yearRepository) {
        return args -> {
            // Check if the year table is already populated to prevent duplicate entries
            if (yearRepository.count() == 0) {
                // Create Year instances
                Year year1400 = new Year(null, 1400L, 0L);
                Year year1401 = new Year(null, 1401L, 0L);
                Year year1402 = new Year(null, 1402L, 0L);
                Year year1403 = new Year(null, 1403L, 0L);

                // Persist them using yearRepository
                yearRepository.save(year1400);
                yearRepository.save(year1401);
                yearRepository.save(year1402);
                yearRepository.save(year1403);

                System.out.println("Year table has been populated");
            }
        };
    }
    @Bean
    public CommandLineRunner populateLetterTypeTable(LetterTypeRepository letterTypeRepository) {
        return args -> {
            // Check if the letter_type table is already populated
            if (letterTypeRepository.count() == 0) {
                // Create LetterType instances
                LetterType incoming = new LetterType(null, "INCOMING");
                LetterType outgoing = new LetterType(null, "OUTGOING");

                // Persist them using letterTypeRepository
                letterTypeRepository.save(incoming);
                letterTypeRepository.save(outgoing);

                // Log to console or perform other necessary operations after insertion
                System.out.println("Letter type table has been populated");
            }
        };
    }
    @Bean
    public CommandLineRunner populatePositionTable(PositionRepository positionRepository) {
        return args -> {
            // Check if the position table is already populated
            if (positionRepository.count() == 0) {
                // Create Position instances
                Position boardChair = new Position(null, "رئیس هیئت مدیره");
                Position viceChair = new Position(null, "نائب رئیس هیئت مدیره");
                Position boardMember = new Position(null, "عضو هیئت مدیره");
                Position ceo = new Position(null, "مدیر عامل");

                // Persist them using positionRepository
                positionRepository.save(boardChair);
                positionRepository.save(viceChair);
                positionRepository.save(boardMember);
                positionRepository.save(ceo);

                // Log to console or perform other necessary operations after insertion
                System.out.println("Position table has been populated");
            }
        };
    }
}
