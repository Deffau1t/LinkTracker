package backend.academy.scrapper.service.notification;

import backend.academy.scrapper.dto.LinkUpdateDTO;

public interface NotificationService {
    void sendNotification(LinkUpdateDTO notification);
}
