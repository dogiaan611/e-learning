package com.example.e_learning.service;

import com.example.e_learning.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumeEmailEvent(String emailMessage) {
        // Trong thực tế, bạn sẽ dùng JavaMailSender để gửi email.
        // Ở đây chúng ta in ra console để giả lập việc xử lý background thành công.
        System.out.println("==========================================================");
        System.out.println("💌 Nhận event từ RabbitMQ!");
        System.out.println("💌 Đang gửi email background tới: " + emailMessage);
        System.out.println("💌 Gửi email thành công!");
        System.out.println("==========================================================");
    }
}
