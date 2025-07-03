# Under construction

🚧 This project is currently being built and is not yet ready for use. 🚧

# Kitafinder-Datenservice

[![Made with love by it@M][made-with-love-shield]][itm-opensource]
<!-- feel free to add more shields, style 'for-the-badge' -> see https://shields.io/badges -->

This Application exports important data from Munichs ["kita finder +"](https://kitafinder.muenchen.de/elternportal/de/) and makes it available for further use.

["kita finder +"](https://kitafinder.muenchen.de/elternportal/de/) is an instance of ["kita-planer"](https://kita-planer.de/) by netgo.

## Built With

This project is built by it@M in cooperation with [netgo](https://www.netgo.de/) based on the following technologies/frameworks:

* Java
* Spring Boot
* Maven
* Docker
* PostgreSQL
* Flyway
* ...

## Set up

To start and test kitafinder-datenservice, you can simply download and run one of the [pre-built packages](https://github.com/orgs/it-at-m/packages?repo_name=kitafinder-datenservice). When running with Docker, first start the required infrastructure by running the [docker-compose.yml](web/stack/docker-compose.yml).

When running from source the default spring profile 'local' uses Docker to start required infrastructure. This includes a postgres-database and sso instance. The configuration can be viewed in [the stack directory](web/stack/).

### kitafinder-datenservice-batch

Tries to connect to a kita-planer instance, queries it's data and persist it in the configured DB for further use. Without configured a kita-planer instance this module does not work!

The mimimum configuration to run this module are following properties:

* app.kitafinder.base-url: API-URL of the kita-planer instance to connect to.
* app.kitafinder.username: Username of the kita-planer API
* app.kitafinder.password: Password of the kita-planer API

### kitafinder-datenservice-web

Provides REST-endpoints serving the stored data. A local deployment of this module doesn't require additional configuration.

## Documentation

### Data import/export

The batch-module performs its loading process in 3 steps:

1. Load all Kindmappe ids
2. Load all Kindmappe entities and generate events
3. Clean up old data

The configuration for the kita-planer connection is defined under 'app.kitafinder.*'.

The first step is used to allow subsequent batching of calls made against netgos kita-planer. The IDs are persisted into the database. The ID-retrieval is batched and can be confugured using 'id-batch-size'.

The second step is also batched with an independent batch size configured in 'data-batch-size'. Kindmappen are loaded, events calculated and both stored in the database batch by batch. For detecting newly occured events, the last saved state of each domain object is used for reference.

In the third and last step old batch data is removed from the database as configured with 'cleanup-keep-age' and 'cleanup-keep-number'. Events are persisted forever and not affected by this step.

### Events

Domain-events are detected after exporting the kita-planer data and persisted into an outbox.

The outbox uses JSONB to store the full payload for events. This ensures we can retroactivaly check sent events, even after database schema changes.

🚧 *Handling of events part is not part of it@Ms initial scope and not yet implemented.* 🚧

### Web-Service

🚧 *This module is not part of it@Ms initial scope and only implemented rudimentarily.* 🚧

The web-module provides a REST-service with endpoints to list and display child data. The OAuth2 security can be configured using the 'app.security' properties.

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
