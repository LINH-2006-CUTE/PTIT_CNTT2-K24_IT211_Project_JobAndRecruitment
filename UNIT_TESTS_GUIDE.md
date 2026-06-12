# Unit Tests Guide - Job and Recruitment Project

## 📋 Tổng Quan

Dự án này sử dụng **JUnit 5** và **Mockito** để viết Unit Tests tự động. Bộ tests được chia thành 2 phần chính:

1. **Service Layer Tests** (5 tests) - Kiểm tra business logic
2. **Controller Layer Tests** (5 tests) - Kiểm tra HTTP endpoints

---

## 🏗️ Cấu Trúc Đặt tên Test (Test Naming Convention)

### **Chuẩn bị Chung**
```
should_<ExpectedBehavior>_When_<Condition>
```

**Giải thích:**
- **should**: Bắt đầu bằng từ "should" (nên/phải) để rõ ràng về mục đích
- **ExpectedBehavior**: Hành động dự kiến (ví dụ: `UpdatePassword`, `ThrowException`, `ReturnHttpStatus200Ok`)
- **When**: Từ khóa "When" ngăn cách điều kiện
- **Condition**: Điều kiện kích hoạt hành động (ví dụ: `OldPasswordIsCorrect`, `UserNotFound`, `EmailExists`)

### **Ví dụ Thực Tế**

| Test Name | Giải thích |
|-----------|-----------|
| `should_UpdatePassword_When_OldPasswordIsCorrect` | Phải cập nhật mật khẩu khi mật khẩu cũ đúng |
| `should_ThrowResourceNotFoundException_When_UserNotFound` | Phải ném exception NotFound khi user không tồn tại |
| `should_ReturnHttpStatus200Ok_When_ValidChangePasswordData` | Phải trả 200 OK khi data đổi mật khẩu hợp lệ |
| `should_ReturnHttpStatus404NotFound_When_EmailDoesNotExist` | Phải trả 404 Not Found khi email không tồn tại |

---

## 📁 Cấu Trúc Thư Mục Tests

```
src/test/java/com/example/jobandrecruitment/
├── service/
│   └── impl/
│       └── UserServiceImplTest.java          (5 tests)
└── controller/
    └── AuthControllerTest.java               (5 tests)
```

---

## 🧪 Chi Tiết 10 Unit Tests

### **PHẦN 1: SERVICE LAYER TESTS (5 tests)**

#### **Công Cụ Sử Dụng:**
- `@ExtendWith(MockitoExtension.class)` - Tích hợp Mockito vào JUnit 5
- `@Mock` - Tạo mock objects cho dependencies
- `@BeforeEach` - Setup dữ liệu trước mỗi test

#### **File: `UserServiceImplTest.java`**

| # | Test Name | Loại | Mục Đích |
|---|-----------|------|---------|
| 1 | `should_UpdatePassword_When_OldPasswordIsCorrect` | ✅ PASS | Kiểm tra đổi mật khẩu thành công |
| 2 | `should_GenerateResetToken_When_EmailExists` | ✅ PASS | Kiểm tra tạo token reset thành công |
| 3 | `should_ResetPassword_When_ValidTokenAndPasswordsMatch` | ✅ PASS | Kiểm tra reset mật khẩu thành công |
| 4 | `should_ThrowResourceNotFoundException_When_UserNotFoundInChangePassword` | ❌ FAIL | Ném exception khi user không tồn tại |
| 5 | `should_ThrowAppException_When_EmailDoesNotExistInForgotPassword` | ❌ FAIL | Ném exception khi email không tồn tại |

**Mô Tả Chi Tiết:**

**TEST 1: Đổi mật khẩu thành công**
```java
// Arrange: Chuẩn bị dữ liệu input
ChangePasswordRequest request = new ChangePasswordRequest(
    "oldPassword123",
    "newPassword456",
    "newPassword456"
);

// Mock: Giả lập hành vi của UserRepository và PasswordEncoder
when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
when(passwordEncoder.matches("oldPassword123", "hashedPassword123")).thenReturn(true);

// Act: Gọi method cần test
userService.changePassword(request);

// Assert: Kiểm tra kết quả
verify(userRepository, times(1)).save(testUser);
```

**TEST 4: Ném exception khi user không tồn tại**
```java
// Arrange
when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

// Act & Assert: Kiểm tra exception được ném
assertThrows(ResourceNotFoundException.class, 
    () -> userService.changePassword(request)
);
```

---

### **PHẦN 2: CONTROLLER LAYER TESTS (5 tests)**

#### **Công Cụ Sử Dụng:**
- `@WebMvcTest(AuthController.class)` - Load chỉ Controller
- `MockMvc` - Giả lập HTTP requests
- `@MockBean` - Mock service layer
- `MockMvcRequestBuilders` - Xây dựng HTTP requests
- `ObjectMapper` - Chuyển object thành JSON

#### **File: `AuthControllerTest.java`**

| # | Test Name | Status | HTTP | Mục Đích |
|---|-----------|--------|------|---------|
| 1 | `should_ReturnHttpStatus200Ok_When_ValidChangePasswordData` | ✅ PASS | 200 | Đổi mật khẩu thành công |
| 2 | `should_ReturnHttpStatus200Ok_When_EmailExistsInForgotPassword` | ✅ PASS | 200 | Gửi email forgot thành công |
| 3 | `should_ReturnHttpStatus200Ok_When_ValidResetPasswordData` | ✅ PASS | 200 | Reset mật khẩu thành công |
| 4 | `should_ReturnHttpStatus400BadRequest_When_OldPasswordIncorrect` | ❌ FAIL | 400 | Mật khẩu cũ sai |
| 5 | `should_ReturnHttpStatus404NotFound_When_EmailDoesNotExist` | ❌ FAIL | 404 | Email không tồn tại |

**Mô Tả Chi Tiết:**

**TEST 1: Đổi mật khẩu thành công (200 OK)**
```java
// Arrange: Chuẩn bị request data
ChangePasswordRequest request = new ChangePasswordRequest(
    "oldPassword123",
    "newPassword456",
    "newPassword456"
);
String jsonRequest = objectMapper.writeValueAsString(request);

// Act: Gửi POST request
mockMvc.perform(post("/api/v1/auth/change-password")
    .contentType(MediaType.APPLICATION_JSON)
    .content(jsonRequest))
    // Assert: Kiểm tra response
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.success", is(true)))
    .andExpect(jsonPath("$.message", is("Đổi mật khẩu thành công")));
```

**TEST 4: Mật khẩu cũ sai (400 Bad Request)**
```java
// Arrange: Mock service ném exception
doThrow(new AppException("Mật khẩu cũ không đúng"))
    .when(userService)
    .changePassword(any(ChangePasswordRequest.class));

// Act & Assert
mockMvc.perform(post("/api/v1/auth/change-password")
    .contentType(MediaType.APPLICATION_JSON)
    .content(jsonRequest))
    .andExpect(status().isBadRequest())
    .andExpect(jsonPath("$.success", is(false)))
    .andExpect(jsonPath("$.message", is("Mật khẩu cũ không đúng")));
```

---

## 🚀 Cách Chạy Tests

### **1. Chạy tất cả tests**
```bash
mvn test
```

### **2. Chạy một test class cụ thể**
```bash
mvn test -Dtest=UserServiceImplTest
mvn test -Dtest=AuthControllerTest
```

### **3. Chạy một test method cụ thể**
```bash
mvn test -Dtest=UserServiceImplTest#should_UpdatePassword_When_OldPasswordIsCorrect
```

### **4. Xem test coverage**
```bash
mvn test jacoco:report
# Mở tệp: target/site/jacoco/index.html
```

---

## 📊 Kiến Trúc Test - AAA Pattern

Mỗi test được viết theo mô hình **AAA (Arrange-Act-Assert)**:

```java
@Test
void testExample() {
    // 1. ARRANGE (Chuẩn bị)
    // - Tạo test data
    // - Setup mocks
    String input = "test@example.com";
    when(repo.findByEmail(input)).thenReturn(Optional.of(user));
    
    // 2. ACT (Thực hiện)
    // - Gọi method cần test
    Service service = new Service(repo);
    Result result = service.doSomething(input);
    
    // 3. ASSERT (Kiểm tra)
    // - Xác minh kết quả
    assertEquals("expected", result);
    verify(repo, times(1)).save(user);
}
```

---

## 🔍 Phân Loại Tests

### **Happy Path (Thành Công)**
- ✅ Kiểm tra trường hợp bình thường, dữ liệu hợp lệ
- Ví dụ: User đăng ký thành công, đổi mật khẩu thành công
- Tests: 1, 2, 3 (trong mỗi phần)

### **Error Cases (Thất Bại)**
- ❌ Kiểm tra xử lý lỗi, exception handling
- Ví dụ: Email không tồn tại, mật khẩu sai
- Tests: 4, 5 (trong mỗi phần)

---

## 🎯 Các Assertions Thường Dùng

| Assertion | Mô Tả | Ví Dụ |
|-----------|-------|-------|
| `assertEquals(expected, actual)` | So sánh hai giá trị | `assertEquals("admin", user.getRole())` |
| `assertTrue(boolean)` | Kiểm tra true | `assertTrue(user.isActive())` |
| `assertFalse(boolean)` | Kiểm tra false | `assertFalse(user.isDeleted())` |
| `assertNotNull(object)` | Kiểm tra không null | `assertNotNull(response)` |
| `assertThrows(Exception, callable)` | Kiểm tra exception | `assertThrows(AppException.class, () -> service.method())` |
| `assertDoesNotThrow(executable)` | Kiểm tra không ném exception | `assertDoesNotThrow(() -> service.method())` |

---

## 🔧 Các Annotations Quan Trọng

### **Service Layer (@ExtendWith + @Mock)**
```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;  // Giả lập repository
    
    private UserServiceImpl userService;     // Real service
    
    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, ...);
    }
}
```

### **Controller Layer (@WebMvcTest + MockMvc)**
```java
@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;  // Giả lập HTTP requests
    
    @MockBean
    private UserService userService;  // Mock service layer
}
```

---

## 📈 Best Practices

1. **Một test = Một behavior**: Mỗi test kiểm tra một hành động duy nhất
2. **Tên descriptive**: Tên test phải mô tả rõ ràng mục đích
3. **Không phụ thuộc vào test khác**: Tests độc lập, không ảnh hưởng lẫn nhau
4. **Mock external dependencies**: Mock DB, API, services bên ngoài
5. **Test controllers và services riêng**: Không mix layer

---

## ❓ FAQ

**Q1: Tại sao phải mock UserRepository?**
A: Vì test muốn kiểm tra logic của service, không muốn kết nối DB thực. Mock giúp cô lập logic.

**Q2: Sự khác biệt giữa @Mock và @MockBean?**
A: 
- `@Mock` - Dùng trong unit tests (MockitoExtension), tạo mock cho injection
- `@MockBean` - Dùng trong integration tests, replace bean trong Spring context

**Q3: Test coverage là gì?**
A: Tỷ lệ code được test bao phủ. Mục tiêu: >80% coverage.

**Q4: Làm thế nào để debug test thất bại?**
A: Thêm `System.out.println()` hoặc dùng debugger của IDE khi chạy test.

---

## 📞 Liên Hệ & Hỗ Trợ

Nếu có vấn đề khi chạy tests, hãy kiểm tra:
1. Maven dependencies được download đủ (`mvn clean install`)
2. JDK version >= 11
3. IDE support cho JUnit 5 (IntelliJ/Eclipse có thể cần update)

---

**Tạo bởi:** QA Team  
**Ngày cập nhật:** 2026-06-12  
**Version:** 1.0

