package net.ryzen.paylinksystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "client_payment_method")
@DynamicUpdate
@Setter
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClientPaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "created_date")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @Column(name = "is_active")
    private Boolean isActive;

    @Type(JsonBinaryType.class)
    @Column(name = "additional_info", columnDefinition = "jsonb")
    private Map<String, Object> additionalInfo;
}
