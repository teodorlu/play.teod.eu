use std::path::PathBuf;
use std::collections::HashMap;

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
    "~/dev/iterate/iterbart/public/data/links.json";

    todo!()
}
