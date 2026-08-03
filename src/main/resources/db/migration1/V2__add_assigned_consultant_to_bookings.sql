-- إضافة العمود الناقص لجدول الحجوزات
ALTER TABLE bookings
    ADD COLUMN assigned_consultant_id BIGINT AFTER farmer_id;

-- إضافة قيد المفتاح الأجنبي (Foreign Key) ليرتبط بجدول الاستشاريين
ALTER TABLE bookings
    ADD CONSTRAINT fk_bookings_assigned_consultant
        FOREIGN KEY (assigned_consultant_id) REFERENCES consultants(id)
            ON DELETE SET NULL
            ON UPDATE CASCADE;