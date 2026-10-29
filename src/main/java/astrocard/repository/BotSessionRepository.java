package astrocard.repository;

import astrocard.entity.BotSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface BotSessionRepository extends JpaRepository<BotSession, Long> {
    @Modifying
    @Query(value = "INSERT INTO bot_sessions (chat_id, state, version) VALUES (:chatId, 'CHOOSE_ACTION', 0) ON CONFLICT (chat_id) DO NOTHING", nativeQuery = true)
    void createIfAbsent(@Param("chatId") long chatId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from BotSession s where s.chatId = :chatId")
    BotSession findForUpdate(@Param("chatId") long chatId);
}
