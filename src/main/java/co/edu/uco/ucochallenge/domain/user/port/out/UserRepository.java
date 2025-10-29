package co.edu.uco.ucochallenge.domain.user.port.out;

import java.util.List;
import java.util.UUID;

import co.edu.uco.ucochallenge.domain.user.model.User;

public interface UserRepository {

        boolean existsByEmail(String email);

        boolean existsByIdTypeAndIdNumber(UUID idType, String idNumber);

        boolean existsByMobileNumber(String mobileNumber);

        User save(User user);

        List<User> findAll();
}
