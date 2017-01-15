#SortedList Playbook

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

Lets now take this learning and use it in UI.

##Sorted Artcile List
`BasicSortedArticlesActivity.java` is an activity show casing two sets of articles. One in the recycler grid view and another in the horizontal recycler view. The working of the demo is as below

* Initially horizontal list contains n articles sorted from n days prior to today.
* If you click on an item in horizontal recycler view it will get removed from there and get added to grid recycler view.
* Similarly a click on an article in gridview will remove it from there and add it to the bottom horizontal view.

If you look into the code, no where do we calculate the position of the item to be inserted or removed. All we do is add/remove items from the dataset and let dataset figure out the callbacks to the recycler view.

If you play around with these article lists, you will notice that calculation of positions can be completely avoided. Since every presentation of a list is sorted in some way, it only makes sense to make `SortedList` as the default data structure to hold data in recycler adapter.

If we just change the comparision logic, which can be easily decoupled with a comparator, we could even use the same recycler view and adapter to handle multiple types of sort options in a recyclerview based list.

## Multiple sort options
`ArticleSortOptionsActivity.java` demonstrates implementation of multiple sort options on a single dataset.

Articles can be compared by timestamp, author, content and category. When a sort option change is triggered by UI, we make a call to dataset to change the sorting comparator. 

`changeSortType` method in the dataset removes all the existing content on the dataset and re adds them back.

## Section List
`SectionArticlesActivity.java` demonstrates implementation of grouping of articles. In this example articles are grouped by their category. Each category becomes a section displaying articles in it. We achieve this grouping by following steps.

* `SectionArticle.java` represents a wrapper over article instance and a category. It is on this class we create our dataset `SectionArticleDataset`. This class is an holder of either a category string or an article instance.
* `SectionArticleDataset` is a straight forward wrapper over `SortedList` with some methods exposed for convienience. It knows how to add article along with its category, it also knows to delete a category when all the articles of that category are removed.
* `compare` method of `SectionArticle.java` embodies the smart logic which achieves the grouping and sorting of our articles by category. This method poses a complicated if else structure, might scare you in the beginning, but when you dig into it, its quite a simple logic, here is what it is doing.
	* If the current instance is a category and the argument to comapre is a category, then it simply compares both of the category strings. Which ever lexically comes first, will take precendence.
	* If the current instance is a category and the argument to compare is an article, then
		* If the article belongs to the same category, current instance should take precendence
		* If the article belongs to a different category, perform category comparision check.
	* If the current instance is an article and the argument to compare is a category, then just reverse the logic of the above step.
	* If current instance and the argument passed, both are articles, then
		* 	If both of them belong to same category, simply compare both of the articles (defaults to published time comparision). This will ensure that we have articles sorted within a category.
		*  If both have different categories, just compare their categories.
* `areItemsTheSame` checks if `SectionArticle` instance reprensent the same logical instance. Two section articles are same
	* If they are both categories and have same category strings.
	* If they are both articles and have article ids.
* `areContentsTheSame` relies on equals check generated by auto value.

Thats it, with these three implementations in place we have a dataset which is ready to presented to RecyclerView.

In the UI example, there are two type of views for recycler adapter to deal with, one for header and one for article. Header takes up all the spans (3) and each article takes a single span.
