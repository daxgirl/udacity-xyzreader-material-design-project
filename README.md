# udacity-xyzreader-material-design-project

#Important note
Android Studio has gone nuts and inspite of using "Clean Project" uploaded all the build folders to git. Bummer...

#Project includes:
1. Main theme inherits from AppCompat. All colors are refered to colors.xml, containing main colors for the app
2. Main theme is overriden in values-v21 for the purpose of using scene transitions on main activity. Main activity has own theme in v21 specifying transition resource.
3. Both activities have AppBar and collapsible toolbar, coordinated by Coordinator layout. Sizes for app bars are specified for phone and tablet view and in some cases are also specific for orientation. Each appbar has elevation of 4dp per google specifications.
4. Custom FAB element replaced with FAB of disign support library. Ripple and elevations (including x translation) are specified according to google instructions.
5. Removed redundant custom views and their references from the code
6. Removed Volley lib jar and it's dependency from app gradle file.
7. Replaced Image Loader Helper with Picasso library to improve loading time and cache ability. In Detail activity the bitmap is loaded into Target and error cases + onBitmapLoaded are overriden. In onBitmapLoaded Pallete library is extracting the relevant colors from the swatch.
8. Moved article image thumbnail from fragment to detail activity. Adapted the ViewPager adapter accordingly to pass colors and image info correctly to the fragment on runtime.
9. Added card view to the fragment in phone landscape orientation and on tablet ui. 
10. Added app:cardPreventCornerOverlap="false" to all card views to prevent visual artifact on older devices due to inability to clip view to shape in design library.
11. Added call to intent service for swipe to refresh view. It is terminated by the broadcast receiver when the update is complete.
12. Added condition for the onClick event of the recycler view item, to check that the refreshing is done before proceeding to detail view. 
13. Removed custom "back" button from detail activity. Now it's managed by home up navigation enabled of support action bar.
14. Per google recommendation refresh button to the menu for accessibility purposes. The action is in app:showAsAction="never" mode per requirements.
15. Added full rtl support (start/end) for all views with left/right attributes. 
16. Added actual data to share action for plain text. 
17. Removed assets folder and made use of roboto built in android font for all texts.
18. Overall cleanup and performance boost where popssible.
