## Customize this file after creating the new REPO and remove this lines.
What to adjust:  
* Add the your project or repo name direct under the logo.
* Add a short and long desciption.
* Add links for your final repo to report a bug or request a feature.
* Add list of used technologies.
* If you have, add a roadmap or remove this section.
* Fill up the section for set up and documentation.
 * Start in this file only with documentation and link to the docs folder.
* Add more project shields. Use [shields.io](https://shields.io/) with style `for-the-badge`.

## ------- end to remove -------
<!-- add Project Logo, if existing -->

# Under construction

This project is currently being built and is not yet ready for use.

# Kitafinder-Datenservice

[![Made with love by it@M][made-with-love-shield]][itm-opensource]
<!-- feel free to add more shields, style 'for-the-badge' -> see https://shields.io/badges -->

This Application exports important data from Munichs kita finder + and makes it available for further use.

### Built With

The documentation project is built with technologies we use in our projects:

* Spring Boot
* Maven
* Docker
* Java
* PostgreSQL
* Flyway

## Set up

To start and test kitafinder-datenservice, you can simply download and run one of the [pre-built packages](https://github.com/orgs/it-at-m/packages?repo_name=kitafinder-datenservice). 

* kitafinder-datenservice-batch tries to connect to a kitafinder instance, queries it's data and persist it in the configured DB for further use.
* kitafinder-datenservice-web provides REST-endpoints serving the stored data.

## Documentation
*what insights do you have to tell*

```mermaid
graph TD;
    batch-job-->DB;
    DB-->web-application;
    web-application-->Users;
    web-application-->other applications;
```

use [diagrams](https://docs.github.com/en/get-started/writing-on-github/working-with-advanced-formatting/creating-diagrams).

Domain-events are detected after exporting the kitafinder-data and persisted into an outbox.

The outbox uses JSONB to store the full payload for events. This ensures we can retroactivaly check sent events, even after database schema changes.

Our events are sent with their full payload, as opposed to links.

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please open an issue with the tag "enhancement", fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Open an issue with the tag "enhancement"
2. Fork the Project
3. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
4. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
5. Push to the Branch (`git push origin feature/AmazingFeature`)
6. Open a Pull Request

More about this in the [CODE_OF_CONDUCT](/CODE_OF_CONDUCT.md) file.

## License

Distributed under the MIT License. See [LICENSE](LICENSE) file for more information.

## Contact

it@M - opensource@muenchen.de

<!-- project shields / links -->
[made-with-love-shield]: https://img.shields.io/badge/made%20with%20%E2%9D%A4%20by-it%40M-yellow?style=for-the-badge
[itm-opensource]: https://opensource.muenchen.de/
