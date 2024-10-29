select
       pal.id_palavra                AS id_palavra,
       pal.paragrafo_id_paragrafo    AS paragrafo_id_paragrafo,
       pal.posicaoParagrafoDaPalavra AS posicaoDaPalavra,
       pal.conteudo                  AS conteudoPalavra,
       pal.conteudoOriginal          AS conteudoOriginalPalavra,
       par.conteudo                  AS conteudoParagrafo,
       par.posicaoParagrafo,
       pag.conteudo                  AS conteudo_pagina,
       pag.numeroPagina              AS numeroPagina,
       liv.nomeLivro                 AS nomeLivro
from livro_index.tb_palavra pal
    join livro_index.tb_paragrafo par  on (pal.paragrafo_id_paragrafo = par.id_paragrafo)
    join livro_index.tb_pagina pag on (par.pagina_idPagina = pag.idPagina)
    join livro_index.tb_arquivo_livro liv on (pag.livro_id_livro = liv.id_livro)
where pal.conteudo like 'placa'
order by pal.conteudo
limit 100;

select distinct conteudo from tb_palavra;

select *
from tb_arquivo_livro liv
inner join tb_pagina pag on (pag.livro_id_livro = liv.id_livro)
where pag.conteudo like '%placa%rede%' limit 100;


select count(1) from tb_palavra;