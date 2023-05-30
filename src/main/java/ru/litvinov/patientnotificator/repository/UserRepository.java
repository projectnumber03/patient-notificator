package ru.litvinov.patientnotificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.litvinov.patientnotificator.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("select u from User as u left join fetch u.roles where u.id = :id")
    Optional<User> findById(@Param("id") final UUID id);

    @Query("select u from User as u left join fetch u.roles")
    List<User> findAllWithRoles();

    List<User> findByLoginLike(final String login);

    @Query("select u from User as u left join fetch u.roles where u.login = :login")
    Optional<User> findByLogin(@Param("login") final String login);

}
