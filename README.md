# Android Scoped State
[![CircleCI](https://circleci.com/gh/KotlinByte/ScopedState.svg?style=shield)]()
[![CircleCI](https://img.shields.io/badge/Maintained-yes-green.svg)]()
[![Android]( https://img.shields.io/github/license/KotlinByte/ScopedState.svg)]()
[![Android]( https://img.shields.io/github/v/release/KotlinByte/ScopedState.svg)]()
##### There is no need for complicated code - just define scopes and then add states between brackets :) 🤤 EZPZ right?

[![Android]( 	https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)]()
[![Android]( 	https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/kotlinbyte)
[![Android]( 	https://img.shields.io/badge/Kotlin-ff8800?&style=for-the-badge&logo=kotlin&logoColor=white)](https://t.me/kotlinbyte)



[![template1.png](https://i.postimg.cc/HxRpRCrs/template1.png)](https://postimg.cc/TLjM5FjZ)

## Scoedstate: How to use?
The concept of scopedstate focuses on scopes and states

As an example, there is a currency screen which has different features like auto-updating prices or manually updating, real-time data or auto-updating date and time.

So let's define scope for those features as follows

``` kotlin
sealed class CurrencyScreenScope {
    object Initial : CurrencyScreenScope()
    object AutomatedPriceUpdates : CurrencyScreenScope()
    object ManualPriceUpdates : CurrencyScreenScope()
    object AutomatedDateAndTimeUpdates : CurrencyScreenScope()
}
```
Now that we have different scopes, yay! 😎</br>
But now you might ask yourself, what is the point of having different scopes for my features, right!?
The answer is that every feature has different states, like maybe it is in the loading state, it is in data state, ..., or maybe it has an error.</br>
Next, let's define different states for each of our scopes as shown below
``` kotlin
sealed class AutomatedPriceUpdateStates {
    object Initial : AutomatedPriceUpdateStates()
    object Loading : AutomatedPriceUpdateStates()
    data class Data(val currencies: List<Currency>): AutomatedPriceUpdateStates()
    object Error: AutomatedPriceUpdateStates()
}

sealed class ManualPriceUpdateStates {
    object Loading : ManualPriceUpdateStates()
    data class Data(val currencies: List<Currency>): ManualPriceUpdateStates()
    data class Error(val reason: String): ManualPriceUpdateStates()
}

sealed class AutomatedDateAndTimeUpdateState {
    data class HoursTicker(val hour:Int) : AutomatedDateAndTimeUpdateState()
    data class MinutesTicker(val minute:Int) : AutomatedDateAndTimeUpdateState()
    data class SecondsTicker(val seconds:Int) : AutomatedDateAndTimeUpdateState()

}

```
Now we have different states for each scope, great! 🥳
