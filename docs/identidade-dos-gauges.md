# Identidade dos gauges por slot

## Escopo da revisão

Revisão em 16/10/2026 usando os JARs locais configurados no projeto:

- Forge 1.20.1, Forge 47.4.0 e Create 6.0.8-289.
- NeoForge 1.21.1, NeoForge 21.1.236 e Create 6.0.10-280.

Os três gauges são itens registrados distintos: `create:factory_gauge`,
`create_larger_recipe_gauges:factory_gauge_6x6` e
`create_larger_recipe_gauges:factory_gauge_9x9`. Eles usam o bloco multipart do
Create, que permite até quatro painéis com tipos diferentes na mesma posição.
O mapeamento global do bloco para seu item continua pertencendo ao Create.

## Causas e correções

A colocação inicial, a colocação com Shift e a interação em um bloco existente
chamam `addPanel` por caminhos diferentes. A implementação anterior gravava a
capacidade somente no callback de dados do BlockItem, que não cobre a interação
direta no painel existente. Os três caminhos agora vinculam o item consumido ao
slot livre antes de ativá-lo e enviar o primeiro pacote de atualização. Se a
adição falhar, restauram o valor anterior sem consumir o item.

Na wrench e na quebra de um subpainel, o Create chama `removePanel` antes de
criar o item devolvido. A implementação anterior apagava a capacidade nesse
intervalo. Agora o item correto é capturado antes da remoção e usado no reembolso
ou drop depois dela.

A identidade fica no `FactoryPanelBehaviour` de cada slot. O Create reutiliza
esse objeto ao mover o painel, então a capacidade acompanha a movimentação
entre slots e entre blocos. Um slot desativado volta à capacidade 3. A próxima
colocação aplica o tipo correto antes de sua ativação.

`GaugeVariant` centraliza a relação entre capacidades 3/6/9 e os itens registrados.
Pickblock, wrench, drops e materiais de schematics usam essa mesma relação.

## Persistência e destruição

A propriedade `LargerRecipeGaugeCapacity` é salva dentro do NBT de cada slot,
tanto na gravação normal e nos pacotes para o cliente quanto no `writeSafe`
usado por schematics. O antigo mapa `CreateBiggerCraftersCapacities` é importado
ao ler dados anteriores quando o slot ainda não tem a propriedade nova.

Na destruição do bloco inteiro, o Create entrega um painel pelo loot e os
demais pelo `FactoryPanelBlockEntity.destroy`. O loot agora resolve o primeiro
slot ativo e a destruição resolve os demais individualmente. Isso preserva
também conjuntos mistos sem duplicar a quantidade total de itens. O tratamento
anterior de `BlockDropsEvent`, exclusivo do NeoForge e limitado a um único painel,
foi substituído por esse caminho comum aos dois loaders.

A checagem de interação do Packager também aceita os itens customizados,
permitindo a mesma passagem para colocação e sintonização que o gauge original.

## Verificação automática

Os testes ficam em `src/gaugeTest` de cada módulo e são ativados com
`-PgaugeTests`. Suas classes e estruturas não entram nos JARs de distribuição.
Eles executam as chamadas reais do Minecraft/Create em um servidor GameTest,
com jogadores simulados em Survival.

| Cenário | Forge | NeoForge |
|---|---|---|
| Colocação inicial de 6×6 e 9×9 e consumo de um item | Passou | Passou |
| Slots mistos, colocação normal/com Shift e recusa de slot ocupado | Passou | Passou |
| Wrench em subpainéis e no último painel | Passou | Passou |
| Quebra de um subpainel e manutenção dos demais | Passou | Passou |
| Destruição de um bloco com quatro slots mistos | Passou | Passou |
| Drop de um gauge único | Passou | Passou |
| Perda do bloco de suporte e drops mistos | Passou | Passou |
| Save/load, pacote de cliente, NBT seguro e materiais de schematics | Passou | Passou |
| Migração do NBT antigo e reutilização de um slot removido | Passou | Passou |
| Movimentação entre slots, entre blocos e wrench após mover | Passou | Passou |
| Passagem da interação de gauges pelo Packager | Passou | Passou |
| Normalização de Ctrl-pick e sintonização mantendo o ID do item | Passou | Passou |

Resultado: **12/12 testes em cada loader**, com `buildAll` aprovado. O relatório
local desta execução está em `build/gauge-audit/gametest-both.log`. A inspeção dos
JARs confirmou todos os mixins registrados presentes e nenhum arquivo de teste
empacotado.

```powershell
.\gradlew.bat -PgaugeTests runForgeGameTests runNeoForgeGameTests buildAll
```

## Revisão ingame por Computer Use

Em 16/10/2026, foram utilizados os mundos `New World` já existentes de cada
loader, em uma área livre próxima de X=100, Z=100.

| Cenário observado | Forge | NeoForge |
|---|---|---|
| Cliente inicia e abre o mundo existente | Passou | Passou |
| Sintonização dos itens customizados em Stock Link | Itens já sintonizados | Passou |
| 9×9 inicial e 6×6 adicional no mesmo bloco, com modelos distintos | Passou | Passou em Survival, consumindo um de cada item |
| 6×6 inicial e 9×9 adicional no mesmo bloco | Passou em Survival | Não repetido manualmente |
| Pickblock retorna o nome correto de cada variante | Passou | Não repetido manualmente |
| Wrench parcial e no último painel, nas duas ordens | Passou em Survival, com reembolso de cada variante | Não repetido manualmente |
| Salvar, reabrir e manter as duas variantes | Passou | Não repetido manualmente |

As ações de pickblock e uso foram acionadas por atalhos temporários F6/F7,
pois mover o mouse absoluto com a câmera capturada alterava a mira. No Forge,
os controles originais e o modo Creative foram restaurados ao concluir. Os
testes no NeoForge foram encerrados a pedido do usuário; F6/F7 e Sneak Toggle
ficaram configurados nessa sessão. O mundo NeoForge já estava em Survival.

Uma operação completa da Schematicannon permanece sem revisão manual.
Destruição por explosão e remoções
externas usam os hooks de loot/destruição inspecionados, mas não têm cenários
dedicados nesta suíte.

Dados que já perderam a identidade e foram salvos como gauge original não contêm
informação suficiente para inferir se antes eram 6×6 ou 9×9. A migração preserva
os tipos que ainda estão registrados no NBT.
