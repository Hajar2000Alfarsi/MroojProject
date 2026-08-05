# Responsive, Maps, and Consultant Layout Update

## Updated behavior
- Consultant profile map now shows a visible draggable marker.
- Consultant profile automatically attempts to detect the browser's current location without saving it until the user presses Save.
- Added a “Use my current location” action with Arabic and English messages.
- New AI Issue Analysis automatically attempts to detect the farmer's current location and keeps the marker draggable/clickable.
- Consultant layout now matches the farmer portal style: sidebar, navbar, language switcher, user avatar, content area, and footer.
- Consultant navigation becomes an off-canvas menu on tablets and phones.
- Farmer layout/sidebar and all shared content received responsive safeguards for desktop, tablet, and mobile widths.
- Added responsive rules for consultant requests, appointments, dashboard, maps, images, forms, and tables.

## Main modified files
- `src/app/features/consultant/profile/profile.ts|html|css`
- `src/app/features/farmer/new-issue/new-issue.ts|html|css`
- `src/app/layouts/consultant-layout/consultant-layout.ts|html|css`
- `src/app/layouts/farmer-layout/farmer-layout.css`
- `src/app/core/farmer-sidebar/farmer-sidebar.css`
- `src/app/features/consultant/*/*.css`
- `src/assets/i18n/en.json`
- `src/assets/i18n/ar.json`
- `src/styles.css`
