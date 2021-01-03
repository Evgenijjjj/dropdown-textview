# Gradle settings

project build.gradle
```groovy

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
```

module build.gradle
```groovy
dependencies {
	        implementation 'com.github.Evgenijjjj:dropdown-textview:0.1'
	}
```

# Usage

```xml
<ru.evgenymotorin.dropdown_textview.DropDownTextView
        android:id="@+id/tv"
        android:layout_width="match_parent"
        android:layout_height="100dp"
        app:ddtv_animate="true"
        app:ddtv_animationDuration="200" />
```

Custom attributes

Attribute | Description
:-------------:|:-------------:
ddtv_animate | Use false if you don't want animate view height changes
ddtv_animationDuration | Duration of the animation

# Sample
![](1.gif)
