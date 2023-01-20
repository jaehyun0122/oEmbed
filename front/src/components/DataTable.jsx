import React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { TableHead } from '@mui/material';

export default function DataTable(props) {
  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 650 }}>
        <TableHead />
        <TableBody>
          {props.keys.map((row) => (
            <TableRow
              key={row}
            >
              <TableCell xs={2}>
                {row}
              </TableCell>
              <TableCell align="left" colSpan={10}>
                {row == 'html' ? (
                        <div dangerouslySetInnerHTML={ {__html: props.data[row]} }>
                        </div>
                ) : (row == 'thumbnail_url') ? (
                  <img src={props.data[row]} />
                ) : props.data[row]}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}