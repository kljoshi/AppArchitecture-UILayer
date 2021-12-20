# Application Architecture - The UI Layer - Guess It!
This is the toy app for lesson 5 of the [Udacity: Developing Android Apps with Kotlin course.](https://classroom.udacity.com/courses/ud9012).
- How to use ViewModel 
- How to use LiveData
- Data Binding with viewModel and xml.

## Highlight for lesson 4: 
### Our App Architecture
The software design principle that we're going to be following is called seperation of concerns. This says that our app should be divided into classes, each that have separate responsibilites. 
Architecture gives you guidelines to figure out which classes should have what responisibilites in your app. 
We will be working with three different classes, the UI controller, the ViewModel and LiveData. 


#### ViewModel
ViewModel will do the actual decision making, the purpose of the ViewModel is to hold the specific data needed to display the fragment or activity it's associated with. Also, ViewModel may do simple calculations and transfroamtions on that data so that it's ready to be displayed by the UI controller. The ViewModel class will contain instances of a third class, LiveData. 

#### LiveData
LiveData classes are crucial for communicating information for the ViewModel to the UI controller, that is should update and redraw the screen. 

### Creating a ViewModel 
ViewModel is an abstract class that you will extend and then implement. It holds your apps UI data and survive’s configuration changes.   Instead of having the UI data at the fragment, move it to your ViewModel and have the fragment reference the ViewModel. The ViewModel survive’s configuration changes so while the fragment is destroyed and then remade, all of the data that you need to display in the fragment, remain in the ViewModel.   Steps:
1. Add dependency Open app gradle file and add the dependency:  ```implementation 'androidx.lifecycle:lifecycle-extensions:2.0.0' ``` And since gradle.   
2. Create SomethingViewModel class, extending ViewModel: Create a new file called SomethingViewModel.kt and extend the ViewModel. ```class GameViewModel : ViewModel()``` Note: ViewModel get destroyed when the activity or fragment the view model is associated with is finally and completely destroyed. Right before this happens, there’s a callback called In the ViewModel class called onCleared. 
3. Associated UI controller and ViewModel.  Create and initialized a NameViewModel, using ViewModelProviders.   Back in SomethingFragment use **_lateinit_** to create a field for SomethingViewModel called ViewModel. Then in onCreateView, request the current SomethingViewModel using the ViewModelProviders class:  ``` viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)```

Note: You never construct ViewModel yourself. If you did, you’d end up constructing a ViewModel every time the fragment was recreated. 


### ViewModel vs UI controller (What kind of data to hold on each)
- UI controller only displays and get user/OS events
- ViewModel holds data for UI
- UI Controller does NOT make decisions
- ViewModel never references fragments and activities or views.

### Benefits of Architecture
1. The code is more organized, manageable and debuggable. 
2. By moving code to the viewModel, you protect yourself from having to worry about lifecycle problem and bugs. 
3. The code is very modular. 
4. The viewModel contains no references to activities, fragments, or views. This happens to be helpful for testing.  

### The power and limits of the ViewModel.

- The first issue is a pre-existing issue that we had before. The ViewModel does preserve data with the configuration changes, but this data is still lost when an app is shut down by the operating system. 
- We need a way to communicate from the ViewModel back to the UI controller without having the ViewModel store references to any views, activities or fragments. We can use **_LiveData_** for this. 

Adding LiveData steps:
1. Add the variable in viewModel and wrap the variable in MutableLiveData. If the variable is Int. example:  ```val score = MutableLiveData<Int>()```
2. Add the observer to UI component. example: ```viewModel.score.observe(this, Observer { newScore ->  binding.scoreText.text = newScore.toString() })```

### LiveData is LifeCycle Aware
 This means that LiveData knows about the lifecycle state of its UI controller observers. LiveData uses this information to interact intelligently with your fragments and activities.   Things to know:
1. LiveData will only update UI controller that are actually on-screen. If your fragment goes off-screen and the value of LiveData changes, four times, it would not update the off-screen fragment. Only when the UI controller goes from off-screen back to on-screen, LiveData will always trigger the observer with the most recent data. 
2. If the LiveData already exists with some data, and a new UI controller starts to observe it, it’ll get the current data immediately. 
3. Finally, if the UI controller gets destroyed, the LiveData will actually internally clean up its own connection to the observer. 

### Adding LiveData Encapsulation to ViewModel 

Encapsulation is the notion of restricted direct access to the object’s fields. This way, you could expose a public set of methods that modify the private internal fields, and you can control the exact way outside classes can and can’t manipulate these internal fields. 

We can use backing property. A backing property allows you to return something from a getter other than the exact object.   Example: ```
private val _score = MutableLiveData<Int>()
val score: LiveData<Int> = _score```

### How to model an event like to navigate to a new screen.  
Steps:
1. Make a properly encapsulated LiveData called eventGameFinish that holds a boolean.
2. Make the function **_onGameFinishComplete_** which makes the value of **_eventGameFinish_** false.
3. Set **_eventGameFinish_** to true, to signify that the game is over.
4. Add an observer of **_eventGameFinish_**.
In the observer lambda you should:
- Make sure that the boolean holding the current value of ```eventGameFinished``` is true. This means the game has finished.
- If the game has finished, call gameFinished.
- Tell the view model that you've handled the game finished event by calling **_onGameFinishComplete._**

What is the benefit of using event function? It is to tell the viewModel that certain event has taken place already. For example: We want to display the Toast only once but if we rotate the screen it will take the current value in the viewModel and display the Toast again. Thats why we need to create an event function that will handle this case of only showing the Toast once. 

### Passing Data to ViewModel 
The general idea to pass data to ViewModel is to create a class known as ViewModel Factory. 

Factories are classes that know how to initiate objects. Therefore, our ViewModel Factory is a class that knows how to create ViewModels.   Steps:
1. Create a ViewModel that takes in a constructor parameter
2. Make a ViewModel Factory for ViewModel 
3. Have factory construct ViewModel with constructor parameter 
4.  Finally, add the viewModel Factory to the call to ViewModelProviders. 
Look at the ScoreViewModel and ScoreViewModelFactory. 

### ViewModel and Data Binding 
Adding data binding with life-cycle library support. This will clean up boilerplate code and let us use data binding to its full potential. 

<img width="710" alt="Data changes in ViewModel" src="https://user-images.githubusercontent.com/43662326/146818985-8ecab440-230a-4a4c-9758-8476310773ab.png">
Right now we have our XML layout which defines views, and we have data for those views in the ViewModel. In-between sits the UI Controller, which is really just acting as a relay between the two. For example, when data in the ViewModel changes an observer the fragment will update the views.

<img width="536" alt="(defined in XML layout)" src="https://user-images.githubusercontent.com/43662326/146819019-fad35586-c554-4a31-b59f-2cfca93f8d2c.png">
 I would be simpler if the data and the views could just communicate directly without relying on the UI controller so much as an intermediary. 

Since the ViewModel holds lot of UI data, so it’s actually a really great object to pass into the data binding. Once it’s done, we can automate some of the communication between the ViewModel and the views to that you don’t need to even involve the UI controller. 

 Sets to add binding to ViewModel:
1. Add a viewModel data binding variable to Fragment layout: <data>
    <variable
        name="gameViewModel"
        type="com.example.android.guesstheword.screens.game.GameViewModel" />
</data> 
2. In the Fragment layout, use the ViewModel variable and data binding to handle clicking: Example: android:onClick=“@{() -> viewModel.onSkip()}”
3. Pass the ViewModel into the data binding: binding.viewModel = ViewModel

### Add LiveData Data Binding

- For TextView use the LiveData from ViewModel to set the text attribute: android:text="@{@string/quote_format(gameViewModel.word)}"
- Call binding.setLifecycleOwner to make the data binding lifecycle aware: To make data binding lifecycle aware and to have it play nicely with LiveData, we need to call binding.setLifecycleOvwner. And pass in “this” — which refers to GameFragment.  binding.setLifecycleOwner(this)


### LiveData Map Transformation 
Beyond holding the UI data, the viewModel is also responsible for doing simple manipulations to make the data ready for the screen. One of the easiest ways to do simple data manipulations to LiveData, such as changing an integer to a string, is by using a method called transformation.map. We can think of LiveData as an object that emits data when it changes. For example, when we update the time, it emits the updated time to all of the UI controllers observing it. 


Recap:
In this lesson we have first experience with Android app architecture. Architecture is powerful because it gives you guidelines by which to separate your code so that each class has specific responsibilities. This keeps things more organized, debuggable, modular, and testable. In our case, our architecture starts with the UI controllers, which are responsible for drawing items onscreen android detecting user and OS events. Then, you learned about ViewModels, which are great location for putting all of the data needed to display Ui controllers because they service configuration changes. Then you learned about LiveData which wraps around our data and allows it to be observed by UI controllers. LiveData has the added bonus of being lifecycle-aware.  Finally, you increased your knowledge of data binding by learning how to make your data binding lifecycle-aware, and how to bind XML layouts to LiveData objects. Thus, making layouts that are automatically updated when LiveData changes. One thing that you might have noticed is that you moved around a lot of code over to the ViewModel. In larger apps, you want to make sure that your VieModel doesn’t take on too much responsibility and become bloated like your game fragment was. We’re going to further refine the role of the ViewModel in future lessons and add additional classes to this architecture. 

The first edition is learning how to save data permanently on the device, so even if the app is destroyed in the background or if your user restarts their device, this data will still be available to them. This is called the data layer, and its what you’ll be exploring in the next lesson. 

