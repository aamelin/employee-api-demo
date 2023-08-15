package company.employee.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import company.employee.domain.Employee;
import company.employee.domain.Hobby;
import company.employee.dto.EmployeeDataDto;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setCollectionsMergeEnabled(false);

        mapper.typeMap(EmployeeDataDto.class, Employee.class)
                .addMappings(m -> {
                    m.map(EmployeeDataDto::getHobbies, Employee::setHobbies);
                    m.skip(Employee::setId);
                    m.skip(Employee::setEmployeeId);
                });
        mapper.typeMap(String.class, Hobby.class)
                .setConverter(ctx -> new Hobby(null, ctx.getSource()));

        mapper.typeMap(Hobby.class, String.class)
                .setConverter(ctx -> ctx.getSource().getHobby());

        return mapper;
    }
}
