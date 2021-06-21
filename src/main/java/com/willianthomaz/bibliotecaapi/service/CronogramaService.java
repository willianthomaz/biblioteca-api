package com.willianthomaz.bibliotecaapi.service;

import com.willianthomaz.bibliotecaapi.model.entity.Emprestimo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CronogramaService {

    private static final String CRONOGRAMA_EMPRESTIMOS_ATRASADOS = "0 0 0 1/1 * ?";

    @Value("${application.mail.lateloans.message}")
    private String message;

    private final EmprestimoService emprestimoService;
    private final EmailService emailService;

    @Scheduled(cron = CRONOGRAMA_EMPRESTIMOS_ATRASADOS)
    public void sendMailToLateLoans(){
        List<Emprestimo> todosOsEmprestimosAtrasados = emprestimoService.getAllLateLoans();
        List<String> mailsList = todosOsEmprestimosAtrasados.stream()
                .map(emprestimo -> emprestimo.getClienteEmail())
                .collect(Collectors.toList());

        emailService.sendMails(message, mailsList);

    }
}

