## Постановка задачи
Целью курсовой работы является создание модели вычислительной системы (ВС) или ее части на некотором уровне детализации, описывающей и имитирующей ее структуру и функциональность.
<br/>Каждый реальный объект (реальная ВС) обладает множеством характеристик, внутренних и внешних связей. Модель есть приближенное описание объекта с целью получения требуемых результатов с определенной точностью и достоверностью. В данном случае требовалось создать имитационную модель в виде системы массового обслуживания.

## Модульная структура
Разработка производилась в среде IntelliJ IDEA 2022.3.2 на языке Java с использованием графической библиотеки JavaFX.
Приложение использует объектно-ориентированную парадигму программирования и содержит набор классов:

* class Application – класс заявки
* class Source – класс источника
* class Device – класс прибора
* class Buffer – класс буфера
* class DispatcherInput – класс диспетчера постановки заявок в очередь
* class DispatcherOutput – класс диспетчера выбора заявок из очереди
* class Statistics – класс для сбора статистики
* class InputStream – класс отвечающий за поток источников
* class OutputStream – класс отвечающий за поток приборов
* class ReportGenerator – для создания файла с полученной статистикой

#### Вариант 10. ИБ ИЗ2 ПЗ1 Д10З3 Д10О2 Д2П2 Д2Б5 ОР1 ОД1

## Расшифровка:
ИБ — Источники бесконечные<br/>
<br/>ИЗ2 - закон распределения источников равномерный<br/>
<br/>ПЗ1 - закон распределения времени обслуживания приборов — экспоненциальный (не зависит от других приборов)<br/>
<br/>Д10З3 - описание дисциплин постановки и выбора. Описание дисциплин (функции диспетчеров) - Буферизация<br/>
Дисциплины буферизации - приоритет на обслуживание - на своободное место. Сдвига очереди в этом случае не происходит.
<br/><br/>Д10О2 - Дисциплины отказа — приоритет по номеру источника<br/>
При этой дисциплине отказ получает заявка с наименьшим приоритетом среди тех, что на данный момент находятся в БП (приоритет определяется номером источника, который ее сгенерировал). Если к этому времени в буфере имеется несколько заявок от источника с минимальным приоритетом, то тогда отказ из них получает самая старая заявка.

 
Д2П2 - Дисциплины постановки на обслуживание – выбор прибора – по кольцу<br/>
Освобождение прибора или его простой означает, что прибор готов взять заявку на обслуживание. Если в буфере есть очередь, то заявка поступает на прибор в момент его освобождения. Какую заявку поставить на обслуживание на освободившийся прибор определяют дисциплины выбора заявок.
<br/>Д2П2 — выбор прибора по кольцу.<br/>
Эта дисциплина производит выбор свободного прибора таким же способом, как и аналогичная дисциплины выбора заявок из буфера по кольцу, т. е. поиск свободных приборов каждый раз начинается с указателя и заявка встает на обслуживание на первый из найденных приборов.




Д2Б5 - Выбор заявки из буфера<br/>
Д2Б5 - приоритет по номеру источника, заявки в пакете. При данной дисциплине прибор обрабатывает заявки по приоритету пакетом (то есть буфер формирует пакет с приоритетными заявками, передает их ДП, а тот в свою очередь отдает данные заявки на обработку свободному прибору. Пока прибор не обработает заявки из данного пакета, за новый пакет он не берется).

ОР1 - Виды отображения результатов работы программной модели. Отражение результатов после сбора статистики ОР1-ОР2 (автоматический режим) - сводная таблица результатов

ОД1 - Виды отображения результатов работы программной модели - Динамическое отражение результатов (пошаговый режим) - отображение динамики функционирования модели - календарь событий, буфер и текущее состояние

Шаг в этом случае — интервал модельного времени от одного особого события до другого ближайшего по времени особого события.




