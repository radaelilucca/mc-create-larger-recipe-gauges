# Compatibilidade e arquitetura

| Variante | Minecraft | Loader | Create | Java |
|---|---|---|---|---|
| Forge | 1.20.1 | 47.4.0 | 6.0.8-289 | 17 |
| NeoForge | 1.21.1 | 21.1.236 | 6.0.10-280 | 21 |

O projeto contém os módulos independentes `forge1201` e `neoforge`. Código Java,
registros, eventos, mixins, receitas, metadados e configuração de cada loader
permanecem isolados. Modelos, traduções e outros recursos compatíveis são
copiados para cada artefato.

Os dois itens mantêm os IDs `factory_gauge_6x6` e `factory_gauge_9x9` no
namespace `create_larger_recipe_gauges`. O bloco de gauge continua sendo o do
Create, preservando compatibilidade com a rede e com os itens originais.

## Estado de aceite

Build, início dos clientes e servidores, automação de pacotes em matrizes 9×9,
multiplayer, capturas equivalentes e revisão manual em jogo são verificações
separadas. Consulte [desenvolvimento](desenvolvimento.md) para o registro das
evidências; compilar não confirma os fluxos em jogo.

