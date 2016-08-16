# Jackal
[Running demo](fractal.ben-weintraub.com).

"Of all the possible pathways of disorder, nature favors just a few" touted James Gleick in [Chaos: Making a New Science](https://smile.amazon.com/dp/B004Q3RRPI/ref=dp-kindle-redirect?_encoding=UTF8&btkr=1#nav-subnav). The introduction of Chaos Theory into the world of deterministic science shattered previous conceptions of what science should be, and spawned the entire discipline of Dynamic/Complex Systems as a subfield in nearly all of the hard sciences. This project is not an effort to break new ground, but an effort to glean a visceral understanding of one key facet of Chaos Theory, and share what I have learned.

## The Mandelbrot Set
Known colloquially as "God's Thumbprint", The Mandelbrot Set is a beautiful intersection of rigorous mathematics and unconstrained art. The Mandelbrot Set is one example of a type of pattern known as a factal; other examples include: the Julia Set, [the Sierpinski Gasket, and von Koch Snowflake](http://world.mathigon.org/Fractals). This project displays it as a graph on the complex plane representing the inclusivity of complex coordinates in a set defined by a specific mathematical function. That function is:
```
Z = Z^2 + Ci
```
This is a recursive function, which means it is expected to be run more than once, and one iteration's output is used as the next iteration's input. A point, represented by ```Ci``` in the equation above can be said to be in the Mandelbrot Set if, and only if, after an arbitrary number of iterations, it has not diverged towards infinity. For most points--exluding (0,0)--it can never be know with certainty whether or not they are in the set; after a 1,000,000 iterations it may not have diverged, but it still might do so on the 1,000,001st. This is where chaos comes into the picture, because despite some semblance of a pattern when graphed on the complex plane, there is no algorithm or heuristic that quickly predicts whether or not a point is in the set other than running the recursive function. This inherent unpredictability explains why the graph contains such jagged edges. These jagged edges however, are not just random perterbations. They are a display of a property called *self similarity*; this is the notion that if one were to zoom in on a small section of the graph, one would see a shape that is similar (though not exactly the same) as the graph as a whole--no matter how far one zooms in, the graph never becomes smooth. This roughness is maintained ad infinitum even under arbitratily large magnification. It can be easily shown, however, that points sufficiently far from the origin (i.e. outside of some radius) diverge quickly enough to be know, for certain, *not* to be in the set. This fact, along with self-similary, implies that the Mandelbot Set has a bounded area, but an *infinitely* long perimiter!

### How to know if a point is in the set
Z, from the equation above, is initialized to ```0 + 0i```, and C is a constant complex number representing a point's coordinate on the complex plane. The recursive function is then run until the abitrary limit is reached, or the length of vector Z has diverged towards infinity.

### Who is Mandelbrot?
[Benoit Mandelbrot](https://en.wikipedia.org/wiki/Benoit_Mandelbrot) was a Mathmatician and Renaissance man known for his discovery of the interesting properties of the eponymous Mandelbrot Set. He had many careers in which he was known for valuable contributions; his careers included engineering at IBM, being a professor of mathematics at Harvard, doing economics research, and several others. He also coined the word [fractal](http://www-03.ibm.com/ibm/history/ibm100/us/en/icons/fractal/).

# Implementation
One key differentiator of this project from others is the choice of languages it was written in. [Clojure/ClojureScript](https://clojure.org/) were chosen because of their practical mix of [Functional Programming](https://en.wikipedia.org/wiki/Functional_programming) and useability. Clojure is in the LISP family of languages of which the poignant feature is the ability to treat code as data, resulting in constructs known as macros which are [useful for an abundance of reasons](http://stackoverflow.com/questions/267862/what-makes-lisp-macros-so-special).

## Visualization
The [Quil library](http://quil.info/) was used for visualization and event handling. The library is based on ProcessingJS, so much of its code is highly optimized. One issue I ran into though, was the performance of the ```fill``` command. In a project like this, ```fill``` needs to be called for every single pixel, and after using Chrome's built-in profiler, that function call was determined to be very costly.

## Performance
Optimizing performance turned out to be the biggest challenge in this project. The initial development included highly functional code (no state or mutability), but when performance improvements needed to be made, it was uncovered through targeted benchmarking that the frequent function calls, as well as the creation and addition to lists had a severe performance impact. Functions which generated data to be fed through a transformation pipeline were replaced by ```doseq``` which is an iterator that does not return a list of values, only ```nil```. The Mandelbrot recusion itself had to be pared down to its bare minimum as well, the process of which taxed the readability of the "numerics" namespace. This was necessary , however, because when iterating over every pixel on the screen and doing a deep recursion on each one, the overhead of any single operation can grow quickly.

### Escape criterion
#### Iterations
In this project, that number of iterations for the Mandelbrot function has been hard-coded to 50. Using more would broaden the range of colors displayed, but can quickly become unreasonably expensive--whatever iteration count is chosen, must be run for *every pixel on the screen*.

#### Escape radius
In practice, it is useful to know as early as possible when a value is diverging. Usually, if the absolute value of vector Z has exceeded 2, it will continue to diverge. So, with this in mind, 2 was used as the escape radius. However, you'll notice that 4 is actuall used blah blah..... this project uses an escape radius of 4....There is nothing intrinsically special about the number fifty. It was chosen for purely utilitarian reasons, namely: it projects an aesthetically pleasing graph, without drowning the CPU (and causing long load times).

## Future work
The improvement which stands out as most salient is certainly the performance. A refactoring of the visualization code so that RBG values are directly inserted into the JavaScrtipt VM's ImageData array rather than relying on Quil's ```fill``` command is likely to offer a substantial gain. It might also be a psychological boon to render each line, one at a time, to show progress.

## Why is it called Jackal?

# Development

Open a terminal and type `lein repl` to start a Clojure REPL
(interactive prompt).

In the REPL, type

```clojure
(run)
(browser-repl)
```

The call to `(run)` starts the Figwheel server at port 3449, which takes care of
live reloading ClojureScript code and CSS. Figwheel's server will also act as
your app server, so requests are correctly forwarded to the http-handler you
define.

Running `(browser-repl)` starts the Figwheel ClojureScript REPL. Evaluating
expressions here will only work once you've loaded the page, so the browser can
connect to Figwheel.

When you see the line `Successfully compiled "resources/public/app.js" in 21.36
seconds.`, you're ready to go. Browse to `http://localhost:3449` and enjoy.

**Attention: It is not needed to run `lein figwheel` separately. Instead we
launch Figwheel directly from the REPL**

## Trying it out

If all is well you now have a browser window saying 'Hello Chestnut',
and a REPL prompt that looks like `cljs.user=>`.

Open `resources/public/css/style.css` and change some styling of the
H1 element. Notice how it's updated instantly in the browser.

Open `src/cljs/jackal/core.cljs`, and change `dom/h1` to
`dom/h2`. As soon as you save the file, your browser is updated.

In the REPL, type

```
(ns jackal.core)
(swap! app-state assoc :text "Interactivity FTW")
```

Notice again how the browser updates.

### Lighttable

Lighttable provides a tighter integration for live coding with an inline
browser-tab. Rather than evaluating cljs on the command line with the Figwheel
REPL, you can evaluate code and preview pages inside Lighttable.

Steps: After running `(run)`, open a browser tab in Lighttable. Open a cljs file
from within a project, go to the end of an s-expression and hit Cmd-ENT.
Lighttable will ask you which client to connect. Click 'Connect a client' and
select 'Browser'. Browse to [http://localhost:3449](http://localhost:3449)

View LT's console to see a Chrome js console.

Hereafter, you can save a file and see changes or evaluate cljs code (without
saving a file).

### Emacs/CIDER

CIDER is able to start both a Clojure and a ClojureScript REPL simultaneously,
so you can interact both with the browser, and with the server. The command to
do this is `M-x cider-jack-in-clojurescript`.

We need to tell CIDER how to start a browser-connected Figwheel REPL though,
otherwise it will use a JavaScript engine provided by the JVM, and you won't be
able to interact with your running app.

Put this in your Emacs configuration (`~/.emacs.d/init.el` or `~/.emacs`)

``` emacs-lisp
(setq cider-cljs-lein-repl
      "(do (user/run)
           (user/browser-repl))")
```

Now `M-x cider-jack-in-clojurescript` (shortcut: `C-c M-J`, that's a capital
"J", so `Meta-Shift-j`), point your browser at `http://localhost:3449`, and
you're good to go.

## Testing

To run the Clojure tests, use

``` shell
lein test
```

To run the Clojurescript you use [doo](https://github.com/bensu/doo). This can
run your tests against a variety of JavaScript implementations, but in the
browser and "headless". For example, to test with PhantomJS, use

``` shell
lein doo phantom
```

## Deploying to Heroku

This assumes you have a
[Heroku account](https://signup.heroku.com/dc), have installed the
[Heroku toolbelt](https://toolbelt.heroku.com/), and have done a
`heroku login` before.

``` sh
git init
git add -A
git commit
heroku create
git push heroku master:master
heroku open
```

## Running with Foreman

Heroku uses [Foreman](http://ddollar.github.io/foreman/) to run your
app, which uses the `Procfile` in your repository to figure out which
server command to run. Heroku also compiles and runs your code with a
Leiningen "production" profile, instead of "dev". To locally simulate
what Heroku does you can do:

``` sh
lein with-profile -dev,+production uberjar && foreman start
```

Now your app is running at
[http://localhost:5000](http://localhost:5000) in production mode.
## License

Distributed under The MIT License (MIT)

## Chestnut

Created with [Chestnut](http://plexus.github.io/chestnut/) 0.14.0 (66af6f40).
