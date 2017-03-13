#SortedList Playbook

##Blog post series
A detailed exploration of `SortedList` through a series of posts is available below.

* [Part 1 — Replacing ArrayList with SortedList](https://medium.com/r/?url=https%3A%2F%2Fblog.uncommon.is%2Fa-guide-to-sortedlist-part-1-5-replacing-arraylist-with-sortedlist-in-recyclerview-f73eca8bc602)
* [Part 2 — Deep dive into data operations](https://medium.com/r/?url=https%3A%2F%2Fblog.uncommon.is%2Fa-guide-to-sortedlist-part-1-5-replacing-arraylist-with-sortedlist-in-recyclerview-f73eca8bc602)
* [Part 3 — Separation of data and view concerns](https://medium.com/r/?url=https%3A%2F%2Fblog.uncommon.is%2Fa-guide-to-sortedlist-part-3-5-separation-of-data-and-view-concerns-2cecb21ea589%3Fsource%3Dcollection_home---4------0----------)
* [Part 4 — Section Lists](https://medium.com/r/?url=https%3A%2F%2Fblog.uncommon.is%2Fa-guide-to-sortedlist-part-4-5-section-lists-2d101f3134f1)
* [Part 5— Pagination](https://blog.uncommon.is/a-guide-to-sortedlist-part-5-5-pagination-dee0862dcee4)
* [Bonus — Deep dive into data operations with JUnit4 tests](https://medium.com/r/?url=https%3A%2F%2Fblog.uncommon.is%2Fa-guide-to-sortedlist-bonus-deep-dive-into-data-operations-through-junit4-tests-df15d20b62d7%23.inm8ei3zo)

##What is a SortedList
[SortedList](https://developer.android.com/reference/android/support/v7/util/SortedList.html) is a data structure to keep the items in the list sorted. Through its various callbacks it can provide information of what exactly has changed in the data structure when items are inserted/removed/updated.

With these callbacks [SortedList.Callback](https://developer.android.com/reference/android/support/v7/util/SortedList.Callback.html), [SortedList.BatchedCallback](https://developer.android.com/reference/android/support/v7/util/SortedList.BatchedCallback.html), [SortedListAdapterCallback](https://developer.android.com/reference/android/support/v7/widget/util/SortedListAdapterCallback.html), SortedList becomes a perfect data structure to back any list based views. After all, every list in sorted in one way or the other.

##SortedList Data Exploration
Before jumping into the example provided by support library, lets just begin by simple data based examples. Lets explore it via the Unit tests.

### Test setup  
We will be adding bunch of articles to a SortedList and understand its callbacks. `Article.java` encapsulates our data item. Every article contains a unique id, some content, an author who produced the content and a published timestamp, which is a unix epoch value in milliseconds.

* `Article.java` is an [Auto Value](https://github.com/google/auto/blob/master/value/userguide/index.md) class. 
* All through the tests we will be working with Immutable instances of articles.  
* We will be using [Java Faker](https://github.com/DiUS/java-faker) to generate fake data for our testing.  
* We will use fluent assertions provided by [AssertJ](http://joel-costigliola.github.io/assertj/)

When articles are presented in UI, generally we would want to sort them by their published time. Thats exactly what we will be exploring using JUnit 4 tests.

#### JUnit 4 Tests
`SortedListTestSetup.java` is a JUnit 4 Rule that sets up all the necessary things for testing SortedList. It does the following

* 5 articles are created, 2 in the past, 1 on the current day and 2 in future.
* `orderedArticleList` instance contains articles ordered according to their published time beginning from older article to the newer article. 
* `shuffledArticles`instance contains the same articles (different object instances, as our articles are immutable) shuffled.
* `SortedListCallbackRecorder` class is a simple implementation of SortedList Callback that records all the callbacks emitted by SortedList. There is a utility method `clear` to erase all the existing records.  
* Test rule exposes `SortedListCallbackRecorder` instance as `callbackRecorder`
* Finally test rule also gives `sortedList` instance which is an empty SortedList that is ready for testing.

#### SortedList Test Class
`SortedListCallbackUnitTest.java` is our test class. It sets up `SortedListTestSetup` rule as `fixture` to ensure every test case gives us a new fixture data. Lets go through each of the tests in details.

###### Test #1 - SortedList should be sorted. 
* The test method `testAddShouldSortListIrrespectiveOfOrder` adds all the 5 articles from `fixture.shuffledArticles()` into sorted list and compares the entries of SortedList with `fixture.orderedArticleList()`

###### Test #2 - Adding multiple same objects into sorted list should not have any effect
* This test is performed by the method `testAdditionOfSameObjectShouldNotChangeSortedList`. 
* It adds a single article to `fixture.sortedList()`
* It creates a duplicate of the same artcile previously added to SortedList and adds it again n times, where n can vary between 1 to 10.
* It asserts that SortedList size remains same and that none of the SortedList callbacks get recorded.

###### Test #3 - Validating insertion callbacks
* This test is performed by the method `testInsertions`
* It iterates through shuffled articles. For each article, it figures out the position in which the item will get inserted and then asserts it with recorded callback from the fixture.

###### Test #4 - Validating changes callbacks
* This test is performed by the method `testChanges`
* It adds all the 5 artciles from the fixture to SortedList.
* It then randomly picks up an article from the SortedList, duplicates the object, changes its data and then adds it to the SortedList
* It validates that only changes callback gets recorded and asserts that changed content is reflected in the list.

###### Test #5 - Validating deletions.
* This test is performed by method `testDeletions`
* It invokes `remove` method on SortedList and asserts that only deletion callbacks is recorded.


###### Test #5 - Validating position moves.
* This test is performed by method `testMoves`
* It fetches an article, duplicates it and changes the published timestamp. A timestamp change is needed to test position change
* It then validates that indeed position is moved properly with move callback recorded and additionally a change callbacks is triggered as well on the old position.

###### Test #6 - Validating batch updates.
* This test is performed by method `testBatchedCallbacks`
* It creates a new SortedList with batched callback that wraps fixture's callback recorder.
* It inserts all 5 artciles from fixture's shuffled articles between SortedList's `beginBatchedUpdates` and `endBatchedUpdates` methods.
* It validates that none of the callbacks get recorded before `endBatchedUpdates`.
* It asserts that after `endBatchedUpdates`, there is only one single callback to insert with a count of 5.

The above tests mostly, if not completely, test all the features of SortedList. Play around with them to understand SortedList.

SortedList is perfect data strcuture to work with RecyclerView. Infact, all the callbacks methods have exact same signature as RecyclerView's notifyXYZ methods.

Simply using [SortedListAdapterCallback] (https://developer.android.com/reference/android/support/v7/widget/util/SortedListAdapterCallback.html), you should be able to connect SortedList with RecyclerView.
