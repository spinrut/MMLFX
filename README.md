# MMLFX
An MML interpreter

## Commands
`abcdefg`: Play the corresponding note. Use `+` for sharps and `-` for flats.

`n#`: Play midi note number `#`. For example, C4 is `n60`.

`r`: Rest.

`l#`: Change note/rest length. Use `.` for dotted notes (e.g. `l1` makes all following notes whole notes, `l2.` makes them dotted half notes, and `l12` makes them quarter note triplets). You can also include a note length after an individual note or rest to change only that note/rest's length.

`<`: Go down an octave

`>`: Go up an octave

`o#`: Choose octave. Default octave is 4.

`t#`: Set tempo to `#` BPM.

`v#`: Set volume. Valid volumes are integers from 1 to 15, inclusive.
