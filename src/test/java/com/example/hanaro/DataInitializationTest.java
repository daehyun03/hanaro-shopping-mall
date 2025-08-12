package com.example.hanaro;

import com.example.hanaro.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("/data/data.sql")
@Transactional
@Commit
public class DataInitializationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private DailySalesStatsRepository dailySalesStatsRepository;
    @Autowired
    private ProductSalesStatsRepository productSalesStatsRepository;

    @Test
    @DisplayName("data.sql 초기화 데이터 검증")
    void verifyDataInitialization() {
        long userCount = userRepository.count();
        assertThat(userCount).isEqualTo(11);
        System.out.println("사용자 데이터 " + userCount + "건이 정상적으로 삽입되었습니다.");

        long productCount = productRepository.count();
        assertThat(productCount).isEqualTo(13);
        System.out.println("상품 데이터 " + productCount + "건이 정상적으로 삽입되었습니다.");

        long orderCount = orderRepository.count();
        assertThat(orderCount).isGreaterThan(0);
        System.out.println("주문 데이터 " + orderCount + "건이 정상적으로 삽입되었습니다.");

        long dailySalesCount = dailySalesStatsRepository.count();
        assertThat(dailySalesCount).isGreaterThan(0);
        System.out.println("일일 매출 통계 데이터 " + dailySalesCount + "건이 정상적으로 삽입되었습니다.");

        long productSalesCount = productSalesStatsRepository.count();
        assertThat(productSalesCount).isGreaterThan(0);
        System.out.println("상품별 매출 통계 데이터 " + productSalesCount + "건이 정상적으로 삽입되었습니다.");

    }
}
