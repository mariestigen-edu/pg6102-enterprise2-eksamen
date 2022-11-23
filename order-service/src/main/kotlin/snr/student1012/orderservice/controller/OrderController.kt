package snr.student1012.orderservice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import snr.student1012.orderservice.integration.PaymentIntegrationService
import snr.student1012.orderservice.integration.RabbitSender
import snr.student1012.orderservice.model.OrderEntity
import snr.student1012.orderservice.service.OrderService

@RestController
@RequestMapping("/api/order")
class OrderController(
    @Autowired private val orderService: OrderService,
    @Autowired private val paymentIntegrationService: PaymentIntegrationService,
    @Autowired private val rabbitSender: RabbitSender) {

    @GetMapping("")
    fun getOrders() : ResponseEntity<List<OrderEntity>> {
        return ResponseEntity.ok().body(orderService.getOrders());
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id : Long?) : ResponseEntity<Any> {
        id?.let {
            orderService.getOrder(id)?.let {
                return ResponseEntity.ok().body(it);
            }.run{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not found");
    }

    @PostMapping("")
    fun registerOrder(@RequestBody orderEntity: OrderEntity?): ResponseEntity<Any> {
        when(orderEntity){
            null -> return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Reqsssuest");
            else -> {
                orderService.registerOrder(orderEntity)?.let {
                    paymentIntegrationService.sendOrderToPaymentService(it)?.let {
                        orderService.updateOrder(it)
                        rabbitSender.sendOrderToShippingService(it);
                        return ResponseEntity.ok().body(it);
                    }.run{
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
                    }
                }.run{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
                }
            }
        }
    }

    @PutMapping("")
    fun updateOrder(@RequestBody orderEntity: OrderEntity?): ResponseEntity<Any> {
        when(orderEntity){
            null -> return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request");
            else -> {
                orderService.updateOrder(orderEntity)?.let {
                    return ResponseEntity.ok().body(it);
                }.run{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
                }
            }
        }
    }

    @DeleteMapping("/{id}")
    fun deleteOrder(@PathVariable id: Long?) : ResponseEntity<Any> {
        id?.let {
            orderService.deleteOrder(id)
            return ResponseEntity.ok().body("Deleted");
            }.run{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not found");
        }
    }
}