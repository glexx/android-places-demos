Google Places SDK Demos Fork to test bug ticket 285920123

https://issuetracker.google.com/issues/285920123
====================================

Test Project is "demo-java"

TestActivity contains simple map initialization with single marker. 

As you can see Long click doesn't work over the marker with the new render but works with LEGACY render.

```
	// LEGACY - long click on marker works
	MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LEGACY, this);
```
```
	// LATEST - long click on marker doesn't work
	MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LATEST, this);
```

```
	map.setOnMapLongClickListener(latLng -> {
		Toast.makeText(TestActivity.this, "OnMapLongClickListener", Toast.LENGTH_SHORT).show();
	});
```