package tripong.backend.repository.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tripong.backend.entity.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);
    Optional<User> findById(Long Id);

    boolean existsByLoginId(String loginId);
    boolean existsByNickName(String nickName);
    boolean existsByEmail(String email);

    @Query("select distinct u from User u join fetch u.userRoles ur join fetch ur.role where u.loginId = :loginId")
    Optional<User> findPrincipleServiceByLoginId(@Param("loginId") String loginId);
}
