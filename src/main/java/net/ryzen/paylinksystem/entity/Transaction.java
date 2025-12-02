package net.ryzen.paylinksystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.ryzen.paylinksystem.module.checkout.dto.ItemDTO;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "transaction")
@DynamicUpdate
@Setter
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "amount")
    private BigInteger amount;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "status")
    private String status;

    @Type(JsonBinaryType.class)
    @Column(name = "original_request", columnDefinition = "jsonb")
    private Map<String, Object> originalRequest;

    @Column(name = "token_id")
    private String tokenId;

    @Column(name = "request_id")
    private String requestId;

    @Type(JsonBinaryType.class)
    @Column(name = "line_items", columnDefinition = "jsonb")
    private List<ItemDTO> lineItems;

    @Column(name = "expired_date")
    private Date expiredDate;

    @Column(name = "trx_due_date")
    private Integer transactionDueDate;

    @Column(name = "currency")
    private String currency;
}
