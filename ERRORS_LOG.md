# 🛠️ Developer Error & Troubleshooting Log

ملف شخصي لتسجيل الأخطاء والمشاكل البرمجية، الأسباب الجذرية، وكيفية حلها للرجوع إليها مستقبلاً.

---

## 📌 Issue #1: Flyway Auto-Configuration & Schema Validation Failure

* **التاريخ:** 2026-07-29
* **المكونات:** Spring Boot 4.0+, Flyway, Hibernate, MySQL, Docker

### 🔴 المشكلة / الـ Error:
عند تشغيل التطبيق كان يظهر خطأ:
`SchemaManagementException: Schema validation: missing table [appointments]`
بالرغم من وجود ملفات Flyway migration، والـ Flyway لم يكن يعمل من الأساس تلقائياً.

### 🔍 السبب الجذري (Root Cause):
1. في **Spring Boot 4.0+** تم تفكيك التهيئة التلقائية (Auto-configuration). وجود مكتبة `flyway-core` وحدها لم يعد كافياً لتشغيل Flyway تلقائياً؛ بل يتطلب وجود `spring-boot-starter-flyway`.
2. حدوث تعارض سابق بين إعدادات MySQL 9.0 و 8.0 في Docker مما أدى إلى تلف الـ Schema وإعادة ضبطها.

### 💡 الحل والخطوات (Resolution):
1. إضافة Dependency الصريح في ملف `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-flyway</artifactId>
   </dependency>
   
command:
إعادة تنظيف حاوية وقاعدة البيانات في Docker:
docker compose down -v
docker compose up -d
//ترك Flyway ينشئ الجداول والـ Spatial Indexes كـ (Source of Truth)، مع جعل Hibernate فقط يتحقق منها عبر spring.jpa.hibernate.ddl-auto=validate

📌 Issue #2: []