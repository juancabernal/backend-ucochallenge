package co.edu.uco.ucochallenge.domain.user.port.out;
import co.edu.uco.ucochallenge.infrastructure.secondary.repository.entity.UserEntity;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

}
