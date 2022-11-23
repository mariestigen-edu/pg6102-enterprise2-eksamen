package snr.student1012.paymentservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import snr.student1012.paymentservice.model.TransactionEntity

interface TransactionRepo : JpaRepository<TransactionEntity, Long>{}