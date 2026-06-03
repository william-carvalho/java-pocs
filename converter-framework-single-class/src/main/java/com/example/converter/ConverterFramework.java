package com.example.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ConverterFramework {
    private ConverterFramework() {
    }

    public static ConversionService defaultService() {
        ConverterRegistry registry = new ConverterRegistry();
        DefaultConversionService service = new DefaultConversionService(registry);
        registry.register(AddressEntity.class, AddressDTO.class, new AddressEntityToAddressDTO());
        registry.register(UserEntity.class, UserDTO.class, new UserEntityToUserDTO(service));
        registry.register(ProductEntity.class, ProductDTO.class, new ProductEntityToProductDTO());
        registry.register(OrderEntity.class, OrderResponse.class, new OrderEntityToOrderResponse(service));
        return service;
    }

    public interface Converter<S, T> {
        T convert(S source);
    }

    public interface ConversionService {
        <T> T convert(Object source, Class<T> targetType);

        <T> List<T> convertList(List<?> source, Class<T> targetType);
    }

    public static final class DefaultConversionService implements ConversionService {
        private final ConverterRegistry registry;

        public DefaultConversionService(ConverterRegistry registry) {
            this.registry = Objects.requireNonNull(registry, "registry");
        }

        public <T> T convert(Object source, Class<T> targetType) {
            if (source == null) {
                return null;
            }
            if (targetType == null) {
                throw new IllegalArgumentException("targetType is required");
            }
            Converter<Object, T> converter = registry.find(source.getClass(), targetType);
            return converter.convert(source);
        }

        public <T> List<T> convertList(List<?> source, Class<T> targetType) {
            if (source == null) {
                return Collections.emptyList();
            }
            List<T> converted = new ArrayList<T>();
            for (Object item : source) {
                converted.add(convert(item, targetType));
            }
            return Collections.unmodifiableList(converted);
        }
    }

    public static final class ConverterRegistry {
        private final Map<ConverterKey, Converter<?, ?>> converters = new LinkedHashMap<ConverterKey, Converter<?, ?>>();

        public <S, T> void register(Class<S> sourceType, Class<T> targetType, Converter<S, T> converter) {
            converters.put(new ConverterKey(sourceType, targetType), converter);
        }

        @SuppressWarnings("unchecked")
        private <T> Converter<Object, T> find(Class<?> sourceType, Class<T> targetType) {
            Converter<?, ?> converter = converters.get(new ConverterKey(sourceType, targetType));
            if (converter == null) {
                throw new ConverterNotFoundException(sourceType, targetType);
            }
            return (Converter<Object, T>) converter;
        }
    }

    private static final class ConverterKey {
        private final Class<?> sourceType;
        private final Class<?> targetType;

        private ConverterKey(Class<?> sourceType, Class<?> targetType) {
            this.sourceType = Objects.requireNonNull(sourceType, "sourceType");
            this.targetType = Objects.requireNonNull(targetType, "targetType");
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ConverterKey)) {
                return false;
            }
            ConverterKey that = (ConverterKey) other;
            return sourceType.equals(that.sourceType) && targetType.equals(that.targetType);
        }

        public int hashCode() {
            return 31 * sourceType.hashCode() + targetType.hashCode();
        }
    }

    public static final class ConverterNotFoundException extends RuntimeException {
        private ConverterNotFoundException(Class<?> sourceType, Class<?> targetType) {
            super("No converter registered from " + sourceType.getSimpleName() + " to " + targetType.getSimpleName());
        }
    }

    private static final class AddressEntityToAddressDTO implements Converter<AddressEntity, AddressDTO> {
        public AddressDTO convert(AddressEntity source) {
            return new AddressDTO(source.getStreet(), source.getCity(), source.getZipCode());
        }
    }

    private static final class UserEntityToUserDTO implements Converter<UserEntity, UserDTO> {
        private final ConversionService conversionService;

        private UserEntityToUserDTO(ConversionService conversionService) {
            this.conversionService = conversionService;
        }

        public UserDTO convert(UserEntity source) {
            AddressDTO address = conversionService.convert(source.getAddress(), AddressDTO.class);
            return new UserDTO(source.getId(), source.getName(), source.getEmail(), address);
        }
    }

    private static final class ProductEntityToProductDTO implements Converter<ProductEntity, ProductDTO> {
        public ProductDTO convert(ProductEntity source) {
            return new ProductDTO(source.getSku(), source.getName(), source.getPrice(), source.getPrice().multiply(new BigDecimal("0.90")));
        }
    }

    private static final class OrderEntityToOrderResponse implements Converter<OrderEntity, OrderResponse> {
        private final ConversionService conversionService;

        private OrderEntityToOrderResponse(ConversionService conversionService) {
            this.conversionService = conversionService;
        }

        public OrderResponse convert(OrderEntity source) {
            List<ProductDTO> products = conversionService.convertList(source.getProducts(), ProductDTO.class);
            BigDecimal total = BigDecimal.ZERO;
            for (ProductEntity product : source.getProducts()) {
                total = total.add(product.getPrice());
            }
            return new OrderResponse(
                    source.getId(),
                    source.getCustomer().getName(),
                    source.getProducts().size(),
                    total,
                    products);
        }
    }

    public static final class AddressEntity {
        private final String street;
        private final String city;
        private final String zipCode;

        public AddressEntity(String street, String city, String zipCode) {
            this.street = street;
            this.city = city;
            this.zipCode = zipCode;
        }

        public String getStreet() {
            return street;
        }

        public String getCity() {
            return city;
        }

        public String getZipCode() {
            return zipCode;
        }
    }

    public static final class UserEntity {
        private final long id;
        private final String name;
        private final String email;
        private final AddressEntity address;

        public UserEntity(long id, String name, String email, AddressEntity address) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.address = address;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public AddressEntity getAddress() {
            return address;
        }
    }

    public static final class ProductEntity {
        private final String sku;
        private final String name;
        private final BigDecimal price;

        public ProductEntity(String sku, String name, String price) {
            this(sku, name, new BigDecimal(price));
        }

        public ProductEntity(String sku, String name, BigDecimal price) {
            this.sku = sku;
            this.name = name;
            this.price = price;
        }

        public String getSku() {
            return sku;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getPrice() {
            return price;
        }
    }

    public static final class OrderEntity {
        private final long id;
        private final UserEntity customer;
        private final List<ProductEntity> products;

        public OrderEntity(long id, UserEntity customer, List<ProductEntity> products) {
            this.id = id;
            this.customer = customer;
            this.products = Collections.unmodifiableList(new ArrayList<ProductEntity>(products));
        }

        public long getId() {
            return id;
        }

        public UserEntity getCustomer() {
            return customer;
        }

        public List<ProductEntity> getProducts() {
            return products;
        }
    }

    public static final class AddressDTO {
        private final String street;
        private final String city;
        private final String postalCode;

        public AddressDTO(String street, String city, String postalCode) {
            this.street = street;
            this.city = city;
            this.postalCode = postalCode;
        }

        public String getStreet() {
            return street;
        }

        public String getCity() {
            return city;
        }

        public String getPostalCode() {
            return postalCode;
        }
    }

    public static final class UserDTO {
        private final long id;
        private final String fullName;
        private final String emailAddress;
        private final AddressDTO address;

        public UserDTO(long id, String fullName, String emailAddress, AddressDTO address) {
            this.id = id;
            this.fullName = fullName;
            this.emailAddress = emailAddress;
            this.address = address;
        }

        public long getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public AddressDTO getAddress() {
            return address;
        }
    }

    public static final class ProductDTO {
        private final String code;
        private final String displayName;
        private final BigDecimal price;
        private final BigDecimal promotionalPrice;

        public ProductDTO(String code, String displayName, BigDecimal price, BigDecimal promotionalPrice) {
            this.code = code;
            this.displayName = displayName;
            this.price = price;
            this.promotionalPrice = promotionalPrice;
        }

        public String getCode() {
            return code;
        }

        public String getDisplayName() {
            return displayName;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public BigDecimal getPromotionalPrice() {
            return promotionalPrice;
        }
    }

    public static final class OrderResponse {
        private final long orderId;
        private final String customerName;
        private final int itemCount;
        private final BigDecimal total;
        private final List<ProductDTO> products;

        public OrderResponse(long orderId, String customerName, int itemCount, BigDecimal total, List<ProductDTO> products) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.itemCount = itemCount;
            this.total = total;
            this.products = Collections.unmodifiableList(new ArrayList<ProductDTO>(products));
        }

        public long getOrderId() {
            return orderId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public int getItemCount() {
            return itemCount;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public List<ProductDTO> getProducts() {
            return products;
        }
    }

    public static final class UnknownDTO {
    }
}
