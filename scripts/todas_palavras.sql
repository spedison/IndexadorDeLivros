select pal.id_palavra                AS id_palavra,
       pal.paragrafo_id_paragrafo    AS paragrafo_id_paragrafo,
       pal.posicaoParagrafoDaPalavra AS posicaoDaPalavra,
       pal.conteudo                  AS conteudoPalavra,
       pal.conteudoOriginal          AS conteudoOriginalPalavra,
       par.conteudo                  AS conteudoParagrafo,
       par.posicaoParagrafo,
       pag.conteudo                  AS conteudo_pagina,
       pag.numeroPagina              AS numeroPagina,
       liv.caminhoArquivo            AS nomeLivro
from livro_index.tb_palavra pal
         join livro_index.tb_paragrafo par on (pal.paragrafo_id_paragrafo = par.id_paragrafo)
         join livro_index.tb_pagina pag on (par.pagina_idPagina = pag.idPagina)
         join livro_index.tb_livro liv on (pag.livro_id_livro = liv.id_livro)
where pal.conteudo like 'placa'
order by pal.conteudo
limit 100;

select distinct conteudo
from tb_palavra;

select *
from tb_livro liv
         inner join tb_pagina pag on (pag.livro_id_livro = liv.id_livro)
where pag.conteudo like '%placa%rede%'
limit 100;


select count(1)
from tb_palavra;

with pagina_pagina_posterior as (select pag1.livro_id_livro id_livro,
                                        pag1.idPagina,
                                        pag1.numeroPagina,
                                        pag1.conteudo       atual,
                                        pag2.conteudo       proxima
                                 from tb_pagina pag1
                                          left join tb_pagina pag2 on (pag1.livro_id_livro = pag2.livro_id_livro and
                                                                       pag1.numeroPagina + 1 = pag2.numeroPagina))
-- select * from pagina_pagina_posterior;
   , palavra_com_livro_e_posicao as (select pag.*,
                                            pal1.id_palavra,
                                            pal1.conteudo,
                                            pal1.conteudoOriginal,
                                            pal1.posicao_palavra_livro
                                     from tb_palavra pal1
                                              inner join tb_paragrafo par on (par.id_paragrafo = pal1.paragrafo_id_paragrafo)
                                              inner join pagina_pagina_posterior pag on (par.pagina_idPagina = pag.idPagina)
--    where pal1.conteudo = 'bolsonaro'
)

select (p2.posicao_palavra_livro-p1.posicao_palavra_livro) xx, p1.posicao_palavra_livro, p2.posicao_palavra_livro, p1.*, p2.*
from palavra_com_livro_e_posicao p1
     ,palavra_com_livro_e_posicao p2
     ,palavra_com_livro_e_posicao p3
where
        p1.conteudo in ( 'bolsonaro','ministro', 'edison', 'fachin' )
    and p2.conteudo in ( 'mst', 'saude', 'covid')
    and p3.conteudo in ('saude')

    and p1.id_livro = p2.id_livro
    and p2.id_livro = p3.id_livro

    and (p2.posicao_palavra_livro-p1.posicao_palavra_livro) between  0 and 5
    and (p3.posicao_palavra_livro-p2.posicao_palavra_livro) between  0 and 5
;


select *
from tb_palavra;