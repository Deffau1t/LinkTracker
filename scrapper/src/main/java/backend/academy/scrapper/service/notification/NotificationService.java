package backend.academy.scrapper.service.notification;

import backend.academy.scrapper.dto.LinkUpdateDTO;

/**
 * NotificationService - интерфейс для отправки уведомлений пользователю
 * о найденной ссылке.
 */
public interface NotificationService {
    /**
     * Метод отправки уведомления пользователю.
     * @param notification - уведомление
     */
    void sendNotification(LinkUpdateDTO notification);
}
