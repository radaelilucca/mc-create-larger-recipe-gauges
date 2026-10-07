# Create: Larger Recipe Gauges

Create: Larger Recipe Gauges adds larger Factory Gauges for automating Create Mechanical Crafting recipes with packages.

## Features

- Configure Mechanical Crafting recipes up to 6×6 with the Expanded Factory Gauge.
- Configure recipes up to 9×9 with the Industrial Factory Gauge.
- Keep the original Create Factory Gauge and its interface.

## Gallery

<p align="center">
  <img src="https://github.com/radaelilucca/mc-create-larger-recipe-gauges/blob/main/docs/gallery/crushing-wheel.png?raw=true" alt="Crushing Wheel configured in the gauge" width="49%">
  <img src="https://github.com/radaelilucca/mc-create-larger-recipe-gauges/blob/main/docs/gallery/potato-cannon.png?raw=true" alt="Potato Cannon configured in the gauge" width="49%">
</p>

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.236 or newer in the 21.1 series
- Create 6.0.10 for Minecraft 1.21.1

Create is required. Download the mod from CurseForge and install it alongside Create in your NeoForge `mods` folder.

## Getting started

1. **Craft a gauge:** two Create Factory Gauges → one Expanded Factory Gauge (6×6); two Expanded Factory Gauges → one Industrial Factory Gauge (9×9). Both recipes are shapeless.
2. **Configure the recipe:** set the gauge's output item and connect the ingredient gauges. Open its recipe settings and enable auto-arrangement for a matching Mechanical Crafting recipe.
3. **Build the crafters:** assemble and power a connected Mechanical Crafter array large enough for the recipe. It can be larger than the recipe grid; connect the crafters from behind with a wrench.
4. **Deliver the packages:** attach a Packager to the connected crafters, set a delivery address in the gauge, and route packages with that address to the Packager.

The crafter array does not need to match the recipe's exact shape: a 9×9 array can craft a 5×5 Crushing Wheel recipe. Empty recipe cells stay empty when packages are unpacked.

## License

This project is licensed under the GNU Affero General Public License v3.0. See [LICENSE](LICENSE).
