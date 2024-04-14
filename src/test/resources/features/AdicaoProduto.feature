#language: pt

@CadastroProdutoValorValido
Funcionalidade: : Produto com valor válido
  Cenario: : Registrar um produto com sucesso
    Dado que tenho um produto com valor válido
    Quando postar um produto
    Então devo ver a mensagem de sucesso



