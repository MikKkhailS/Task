package ru.relex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.relex.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByWalletNumber(String walletNumber);

    @Query(value = "SELECT rub_balance FROM Users WHERE rub_balance != 'null'", nativeQuery = true)
    List<String> findAllRub();

    @Query(value = "SELECT btc_balance FROM Users WHERE btc_balance != 'null'", nativeQuery = true)
    List<String> findAllBtc();

    @Query(value = "SELECT ton_balance FROM Users WHERE ton_balance != 'null'", nativeQuery = true)
    List<String> findAllTon();
}