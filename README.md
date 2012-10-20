# Enlive-partials

Adds support for including and embedding partial templates in the enlive templating library. Based on the awesome work by [Brenton Ashworth](https://github.com/brentonashworth), [Craig Andera](https://github.com/candera) and others at [Relavance, Inc.](http://thinkrelevance.com/) on the [Clojurescript one project](http://clojurescriptone.com/).

## Why?

One of the biggest benefits of using Enlive is the ability to have designers work with plain HTML/CSS, without having to worry about the underlying template engine. This is great, but can lead to a lot of duplication in you templates (just imagine having to include all the Twitter Bootstrap dependencies in all your templates files.

A better approach is to create a layout.html file with all the necessary resources, and interpolate the html specific to each template into the body of layout.html.

## Install

###Leiningen

    [com.ebaxt.enlive-partials "0.1.1"]

###Maven

```xml
<dependency>
  <groupId>com.ebaxt.enlive-partials</groupId>
  <artifactId>com.ebaxt.enlive-partials</artifactId>
  <version>0.1.1</version>
</dependency>
```

Remember to add clojars as a repository

```xml
 <repository>
   <id>clojars</id>
   <url>http://clojars.org/repo/</url>
 </repository>
```


## Usage

Example taken from the [Clojurescript one documentation](https://github.com/brentonashworth/one/wiki/Design-and-templating).

Suppose we have a layout file `templates/layout.html` which contains the following HTML:

```html
<html>
  <head>Example</head>
  <body>
    <_include file="menu.html"/>
    <div id="content"></div>
    <div id="footer"></div>
  </body>
</html>
```

Furthermore, suppose we have the following two files named `templates/menu.html` and `templates/example.html`.

```html
<!-- templates/menu.html -->
<div>Navigation Menu</div>
```

```html
<!-- templates/example.html -->
<_within file="layout.html">
  <div id="content">New Content</div>
  <div id="footer">A Footer</div>
</_within>
```


In order to handle the partial templates you have to enable the `com.ebaxt.enlive-partials/handle-partials` ring middleware.

Assumig you have the following directory structure:

    resources/
      templates/
        example.html
        menu.html
        layout.html

You enable the middleware by passing in the path to the template directory, together with an optional `:templateContext`. If no template-context is provided, the template path will be used.

```clojure
(use 'ring.middleware.file 'ring.adapter.jetty 'com.ebaxt.enlive-partials)

(defn hello [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(def app (handle-partials hello "templates" {:template-context "tpl"}))

(run-jetty app {:port 8080})
```

If we point the browser to `http://localhost:8080/tpl/example.html`, the following file will be served:


```html
<html>
  <head></head>
  <body>
    <div>Navigation Menu</div>
    <div id="content">New Content</div>
    <div id="footer">A Footer</div>
  </body>
</html>
```



## License

Copyright © 2012 Erik Bakstad

Distributed under the Eclipse Public License, the same as Clojure.
