# Om tutorial 2

Working my way through [this tutorial](https://github.com/omcljs/om/wiki/Intermediate-Tutorial).

I included the docs and config from [the free download](https://my.datomic.com/downloads/free), and am using homebrew's version of datomic.

To get started:
- In one shell, run
  - `$ datomic-transactor `pwd`/datomic-free-0.9.5390/config/samples/free-transactor-template.properties`
- In another shell
  - `$ cd om-async`
  - `$ rlwrap lein figwheel`
