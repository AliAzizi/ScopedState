# Android Scoped State
[![CircleCI](https://circleci.com/gh/KotlinByte/ScopedState.svg?style=shield)]()
[![CircleCI](https://img.shields.io/badge/Maintained-yes-green.svg)]()
[![Android]( https://img.shields.io/github/license/KotlinByte/ScopedState.svg)]()
#### There is no need for complicated code - just define scopes and then add states between brackets :) 🤤 EZPZ right?

[![Android]( 	https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)]()
[![Android]( 	https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/kotlinbyte)
[![Android]( 	https://img.shields.io/badge/Kotlin-ff8800?&style=for-the-badge&logo=kotlin&logoColor=white)](https://t.me/kotlinbyte)

### Installation

Gradle
------
```
dependencies {
    ...
    implementation 'io.github.kotlinbyte:scoped-state:1.0.0'
}
```

[![template1.png](https://i.postimg.cc/HxRpRCrs/template1.png)](https://postimg.cc/TLjM5FjZ)

## Scopedstate: How to use?
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
Now we have different states for each scope, great! 🥳</br>
As we move forward, let's create a viewmodel that contains a MutableScopedStateFlow so we can emit cool stuff from it
``` kotlin
class CurrencyScreenViewModel : ViewModel() {
    // By marking it as private, only viewmodel will be able to emit data through it
    private val _scopedState: MutableScopedStateFlow<CurrencyScreenScope> =
        MutableScopedStateFlow.create<CurrencyScreenScope, CurrencyScreenScope.Initial>()

    val state: ScopedStateFlow<ExampleScope> = _scopedState
}
```
A MutableScopedStateFlow can be constructed in a variety of ways, as shown below:

``` kotlin
// When it is created, initialScope is emitted
val _scopedState: MutableScopedStateFlow<Scope> = MutableScopedStateFlow.create<Scope, Scope.InitialScope>()
    
// Result is same as previous method
val _scopedState: MutableScopedStateFlow<Scope> = MutableScopedStateFlow.create<Scope, Scope.InitialScope>(Scope.InitialScope::class.java)
    
// It emits initialScope with the state of ExampleState.Init when it is created
val _scopedState: MutableScopedStateFlow<Scope> = MutableScopedStateFlow.create<Scope, Scope.InitialScope>(ExampleState.Init)
    
// Result is same as previous method
val _scopedState: MutableScopedStateFlow<Scope> = MutableScopedStateFlow.create<Scope, Scope.InitialScope>(Scope.InitialScope::class.java, ExampleState.Init)
```

> **_NOTE:_** When you're trying to create MutableScopedState, you need to specify a scope for initializing stateflow!</br>
> **_TIPS:_** The initial state can be specified as well, but it's optional

As with stateflow, MutableScopedStateFlow has emit() method too :

``` kotlin
//emits scope
_scopedState.emit<Scope>()

//emits state
_scopedState.emit(state)

//emits scope and state
_scopedState.emit<Scope>(state)
```

Let's apply them to our use case 

``` kotlin
class CurrencyScreenViewModel(repo: CurrencyRepo) : ViewModel() {
    // By marking it as private, only viewmodel will be able to emit data through it
    private val _scopedState: MutableScopedStateFlow<CurrencyScreenScope> =
        MutableScopedStateFlow.create<CurrencyScreenScope, CurrencyScreenScope.Initial>()

    val scopedStateFlow: ScopedStateFlow<ExampleScope> = _scopedState
    
    fun fetchCurrencyListInInterval(){
        _scopedState.emit<CurrencyScreenScope.AutomatedPriceUpdates>(AutomatedPriceUpdateStates.Loading)
        repo.fetch().fold(
            error = {
                _scopedState.emit<CurrencyScreenScope.AutomatedPriceUpdates>(AutomatedPriceUpdateStates.Error)
            },
            data = { currencies ->
                _scopedState.emit<CurrencyScreenScope.AutomatedPriceUpdates>(AutomatedPriceUpdateStates.Data(currencies))
            }
        )
    }
}
```

EZ, that's done. You may have noticed that we are duplicating our scope each time! Personally, I don't like it 😂😂</br>
Therefore, for this usecase, our function is single-purpose and the scope of the emissions is the same. This is where the withScope() function comes into play:
``` kotlin
_scopedState.withScope<Scope, BaseState> {

}
```
Here's our modified code:

``` kotlin
class CurrencyScreenViewModel(repo: CurrencyRepo) : ViewModel() {
    // By marking it as private, only viewmodel will be able to emit data through it
    private val _scopedState: MutableScopedStateFlow<CurrencyScreenScope> =
        MutableScopedStateFlow.create<CurrencyScreenScope, CurrencyScreenScope.Initial>()

    val scopedStateFlow: ScopedStateFlow<ExampleScope> = _scopedState
    
    fun fetchCurrencyListInInterval() = _scopedState.withScope<CurrencyScreenScope.AutomatedPriceUpdates, AutomatedPriceUpdateStates> {
        emit(AutomatedPriceUpdateStates.Loading)
        repo.fetch().fold(
            error = {
                emit(AutomatedPriceUpdateStates.Error)
            },
            data = { currencies ->
                emit(AutomatedPriceUpdateStates.Data(currencies))
            }
        )
    }
}
```

We have finished our viewmodel, It's time to move on to the activity/fragment</br>
In order to change the UI based on scope and state of an activity or fragment, we need to collect them from ScopedStateFlow. This can be accomplished with StateWatcher:

``` kotlin
StateWatcher.watch(ScopedStateFlow){
    //stuff
}
```

Here is how we can apply that to our activity:

``` kotlin
class CurrencyActivity : AppCompatActivity() {
    
    lateinit var viewModel: CurrencyScreenViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // setup views + viewmodel
        setupStateWatcher()
    }
    
    fun setupStateWatcher(){
        StateWatcher.watch(viewModel.scopedStateFlow){
            attach(lifecycle) //important
            
            // This is where you can define your scopes
            
            scope<CurrencyScreenScope.AutomatedPriceUpdates, AutomatedPriceUpdateStates>{
            
                // This is where you can define your states
                
                state<AutomatedPriceUpdateStates.Loading> {
                    // show loading progresss
                }
                
                state<AutomatedPriceUpdateStates.Data>{ currencyList ->
                    // show currency list
                }
                
            }
        }
    }
    
}
```
