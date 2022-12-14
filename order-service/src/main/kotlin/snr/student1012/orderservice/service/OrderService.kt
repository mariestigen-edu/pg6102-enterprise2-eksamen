package snr.student1012.orderservice.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import snr.student1012.orderservice.model.OrderEntity
import snr.student1012.orderservice.repository.OrderRepo

@Service
class OrderService(@Autowired private val orderRepo: OrderRepo) {

    fun getOrders() : List<OrderEntity>{
        return orderRepo.findAll();
    }

    fun getOrders(page: Int) : Page<OrderEntity> {
        return orderRepo.findAll(Pageable.ofSize(5).withPage(page));
    }

    fun getOrder(id: Long) : OrderEntity? {
        return orderRepo.findByIdOrNull(id);
    }

    fun registerOrder(orderEntity: OrderEntity) : OrderEntity?{
        return orderRepo.save(orderEntity);
    }

    fun updateOrder(orderEntity: OrderEntity) : OrderEntity?{
        return orderRepo.save(orderEntity);
    }

    fun deleteOrder(id: Long) {
        orderRepo.deleteById(id);
    }
}
