use std::path::PathBuf;
use std::collections::HashMap;
use maplit::hashmap;

pub struct Config {
    providers: HashMap<String, Provider>
}

pub struct Provider {
    ls: Ls
}

pub enum Ls {
    FromFile(PathBuf),
    FromUrl(String)
}

pub fn read_config() -> Config {

    // lage config

    Config{
        providers: hashmap!{
            "iterbart".into() => Provider{
                // ønsker å lage en String av &str "~/dev..." så eierskapet følger verdien (configen)
                ls: Ls::FromFile("~/dev/iterate/iterbart/public/data/links.json".into())
            },
        }
    };

    "~/dev/iterate/iterbart/public/data/links.json";

    todo!()
}
