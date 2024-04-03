Fork of https://github.com/phantamanta44/libnine

## Changed/added text field functionality:

- Values are entered immediately, no need to click button to confirm
- `Tab` and `Enter` can be used for navigation, excel-like
- `Delete` deletes value from text field
- `k` adds 000 to the value, or sets to 1000 if empty

---

---

Notes to developers / reminder to me:

Commands can be run from gradle IDEA tab, it will use project java which is better.

- on checkout, `gradle build` to get deps etc.
- then `gradle genIntellijRuns` to get IDEA setup
- when finished, `gradle publishToMavenLocal` to make this version available to deps

Issues:

- `ParameterizedItemModelLoader:37` private access - META_INF/libnine_at.cfg should make fields public
In `build.gradle` `accessTransformer = file('src/main/resources/META-INF/libnine_at.cfg')` is responsible for this
- Try clicking gradle reload if anything weird